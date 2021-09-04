package com.tea.ilearn.ui.home;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.heaven7.android.dragflowlayout.ClickToDeleteItemListenerImpl;
import com.heaven7.android.dragflowlayout.DragAdapter;
import com.heaven7.android.dragflowlayout.DragFlowLayout;
import com.heaven7.android.dragflowlayout.IDraggable;
import com.tea.ilearn.Constant;
import com.tea.ilearn.R;
import com.tea.ilearn.databinding.FragmentHomeBinding;
import com.tea.ilearn.utils.DB_utils;

import java.util.ArrayList;
import java.util.List;

import per.goweii.actionbarex.common.ActionBarSearch;
import per.goweii.actionbarex.common.AutoComplTextView;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ActionBarSearch searchBar;
    private AutoComplTextView acTextView;
    private View root;

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

        return root;
    }

    private void initTabs() {
        List<String> subjects = Constant.EduKG.SUBJECTS; // TODO colin: database ralated (no thread)
        List<EntityListFragment> fragments = new ArrayList<>();
        for (String subject : subjects) {
            fragments.add(new EntityListFragment());
        }
        SubjectListAdapter pagerAdapter = new SubjectListAdapter(getChildFragmentManager(), subjects, fragments);
        binding.viewPager.setAdapter(pagerAdapter);
        binding.subjectTabs.setupWithViewPager(binding.viewPager);

        binding.flowLayout.getDragItemManager().addItems(subjects);
        binding.unused.getDragItemManager().addItems(subjects);

        binding.editMenu.setOnCheckedChangeListener((btn, checked) -> {
            if (checked) {
                binding.editPanel.setVisibility(View.VISIBLE);
                binding.cover.setVisibility(View.VISIBLE);
            } else {
                binding.flowLayout.finishDrag();
                binding.cover.setVisibility(View.GONE);
                binding.editPanel.setVisibility(View.GONE);
            }
        });
    }

    private void search() {
        // TODO  acha
    }

    private static class NonDrag implements IDraggable {
        String text;
        public NonDrag(String text) { this.text = text; }
        @Override public boolean isDraggable() { return false; }
    }
}