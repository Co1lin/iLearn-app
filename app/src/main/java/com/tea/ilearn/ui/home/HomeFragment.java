package com.tea.ilearn.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.heaven7.android.dragflowlayout.ClickToDeleteItemListenerImpl;
import com.heaven7.android.dragflowlayout.DragAdapter;
import com.heaven7.android.dragflowlayout.DragFlowLayout;
import com.tea.ilearn.Constant;
import com.tea.ilearn.R;
import com.tea.ilearn.databinding.FragmentHomeBinding;
import com.tea.ilearn.utils.DB_utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import per.goweii.actionbarex.common.ActionBarSearch;
import per.goweii.actionbarex.common.AutoComplTextView;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ActionBarSearch searchBar;
    private View root;
    private AutoComplTextView acTextView;
    private SubjectListAdapter pagerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        searchBar = binding.searchBar;
        acTextView = searchBar.getEditTextView();

        searchBar.setOnRightIconClickListener(view -> search());
        // bind enter key
        searchBar.getEditTextView().setOnKeyListener((view, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                search();
                return true;
            }
            return false;
        });

        // auto completion
        acTextView.setDropDownAnchor(searchBar.getId());
        acTextView.setThreshold(1); // default 2, minimum 1
        acTextView.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus)
                acTextView.showDropDown();
        });
        DB_utils.updateACAdapter(getActivity(), getContext(), acTextView);

        binding.flowLayout.setOnItemClickListener(new ClickToDeleteItemListenerImpl(R.id.all){
            @Override
            protected void onDeleteSuccess(DragFlowLayout dfl, View child, Object data) {
                binding.unused.getDragItemManager().addItem(data);
                binding.unused.beginDrag();
            }
        });

        binding.flowLayout.setDragAdapter(new DragAdapter<String>() {
            @Override
            public int getItemLayoutId() {
                return R.layout.used_chip;
            }

            @Override
            public void onBindData(View itemView, int dragState, String data) {
                itemView.setTag(data);

                ((TextView) itemView.findViewById(R.id.text)).setText(Constant.EduKG.EN_ZH.get(data));
                itemView.findViewById(R.id.iv_close).setVisibility(
                        dragState != DragFlowLayout.DRAG_STATE_IDLE ? View.VISIBLE : View.INVISIBLE
                );
            }

            @NonNull @Override
            public String getData(View itemView) {
                return (String) itemView.getTag();
            }
        });

        binding.unused.setOnItemClickListener(new ClickToDeleteItemListenerImpl(R.id.all){
            @Override
            protected void onDeleteSuccess(DragFlowLayout dfl, View child, Object data) {
                binding.flowLayout.getDragItemManager().addItem(data);
            }
        });

        binding.unused.setDragAdapter(new DragAdapter<String>() {
            @Override
            public int getItemLayoutId() {
                return R.layout.unused_chip;
            }

            @Override
            public void onBindData(View itemView, int dragState, String data) {
                itemView.setTag(data);

                ((TextView) itemView.findViewById(R.id.text)).setText(Constant.EduKG.EN_ZH.get(data));
                itemView.findViewById(R.id.iv_open).setVisibility(View.VISIBLE);
            }

            @NonNull @Override
            public String getData(View itemView) {
                return (String) itemView.getTag();
            }
        });

        initTabs();

        ((EntityListFragment)pagerAdapter.getItem(0)).waitForBinding("", acTextView);

        return root;
    }

    private void initTabs() {
        List<String> subjects = Arrays.asList("biology", "chemistry"); // TODO colin: database ralated (no thread)

        pagerAdapter = new SubjectListAdapter(getChildFragmentManager(), subjects);
        binding.viewPager.setOffscreenPageLimit(Constant.EduKG.SUBJECTS_EN.size());
        binding.viewPager.setAdapter(pagerAdapter);
        binding.subjectTabs.setupWithViewPager(binding.viewPager);

        binding.editMenu.setOnCheckedChangeListener((btn, checked) -> {
            if (checked) {
                binding.editPanel.setVisibility(View.VISIBLE);
                binding.cover.setVisibility(View.VISIBLE);

                List<String> subs = new ArrayList<>();
                for (int i = 0; i < binding.subjectTabs.getTabCount(); ++i) {
                    subs.add(Constant.EduKG.ZH_EN.get(binding.subjectTabs.getTabAt(i).getText().toString()));
                }
                binding.flowLayout.getDragItemManager().addItems(subs);
                for (String subject : Constant.EduKG.SUBJECTS_EN) {
                    if (!subs.contains(subject))
                        binding.unused.getDragItemManager().addItems(subject);
                }

                binding.unused.beginDrag();
                binding.flowLayout.beginDrag();
            } else {
                binding.flowLayout.finishDrag();
                binding.flowLayout.finishDrag();
                binding.cover.setVisibility(View.INVISIBLE);
                binding.editPanel.setVisibility(View.INVISIBLE);

                pagerAdapter.change(binding.flowLayout.getDragItemManager().getItems());

                binding.flowLayout.getDragItemManager().clearItems();
                binding.unused.getDragItemManager().clearItems();

                search();
            }
        });

        binding.subjectTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override public void onTabSelected(TabLayout.Tab tab) { search(); }
            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private String getQuery() {
        return binding.searchBar.getEditTextView().getText().toString();
    }

    private void search() {
        int pos = binding.subjectTabs.getSelectedTabPosition();
        Log.d("MYDEBUG", String.valueOf(pos)+" "+pagerAdapter.getCount());
        ((EntityListFragment)pagerAdapter.getItem(pos)).search(getQuery(), acTextView);
    }
}