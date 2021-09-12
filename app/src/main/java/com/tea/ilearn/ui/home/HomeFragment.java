package com.tea.ilearn.ui.home;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.heaven7.android.dragflowlayout.ClickToDeleteItemListenerImpl;
import com.heaven7.android.dragflowlayout.DragAdapter;
import com.heaven7.android.dragflowlayout.DragFlowLayout;
import com.tea.ilearn.Constant;
import com.tea.ilearn.R;
import com.tea.ilearn.databinding.FragmentHomeBinding;
import com.tea.ilearn.model.Preference;
import com.tea.ilearn.net.backend.Backend;
import com.tea.ilearn.utils.DB_utils;
import com.tea.ilearn.utils.ObjectBox;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
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

        TypedValue typedValue = new TypedValue();
        binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.colorSurface, typedValue, true);
        int color = ContextCompat.getColor(binding.getRoot().getContext(), typedValue.resourceId);
        acTextView.setBackgroundTintList(ColorStateList.valueOf(color));

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

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        reloadTabs();
        DB_utils.updateACAdapter(getActivity(), getContext(), acTextView);
    }

    private void reloadTabs() {
        if (pagerAdapter != null) { new Thread(() -> {
            Box<Preference> preferenceBox = ObjectBox.get().boxFor(Preference.class);
            // load subject preference from DB
            List<String> subjects;
            List<Preference> res = preferenceBox.getAll();
            if (res != null && res.size() > 0 &&
                    res.get(0).getSubjects() != null && res.get(0).getSubjects().size() > 0) {
                subjects = res.get(0).getSubjects();
            } else {
                preferenceBox.removeAll();
                Preference preference = new Preference();
                subjects = preference.getSubjects();
                preferenceBox.put(preference);
            }
            // set to UI
            if (!subjects.equals(pagerAdapter.getSubjects()))
                getActivity().runOnUiThread(() -> pagerAdapter.change(subjects));
        }).start();}
    }

    private void initTabs() {
        new Thread(() -> {
            Box<Preference> preferenceBox = ObjectBox.get().boxFor(Preference.class);
            // load subject preference from DB
            List<String> subjects;
            List<Preference> res = preferenceBox.getAll();
            if (res != null && res.size() > 0 &&
                    res.get(0).getSubjects() != null && res.get(0).getSubjects().size() > 0) {
                subjects = res.get(0).getSubjects();
            }
            else {
                preferenceBox.removeAll();
                Preference preference = new Preference();
                subjects = preference.getSubjects();
                preferenceBox.put(preference);
            }
            // set to UI
            getActivity().runOnUiThread(() -> {
                pagerAdapter = new SubjectListAdapter(getChildFragmentManager(), subjects);
                binding.viewPager.setOffscreenPageLimit(Constant.EduKG.SUBJECTS_EN.size());
                binding.viewPager.setAdapter(pagerAdapter);
                binding.subjectTabs.setupWithViewPager(binding.viewPager);
                ((EntityListFragment) pagerAdapter.getItem(0)).waitForBinding("", acTextView);
            });
        }).start();

        Box<Preference> preferenceBox = ObjectBox.get().boxFor(Preference.class);

        binding.editPanel.setOnTouchListener((view, event) -> true);

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
                if (binding.flowLayout.getDragItemManager().getItems().size() == 0) {
                    binding.editMenu.setChecked(true);
                    Toast.makeText(binding.getRoot().getContext(), "请至少保留一门学科", Toast.LENGTH_SHORT).show();
                    return;
                }
                binding.flowLayout.finishDrag();
                binding.flowLayout.finishDrag();
                binding.cover.setVisibility(View.INVISIBLE);
                binding.editPanel.setVisibility(View.INVISIBLE);

                List<String> newSubjects = binding.flowLayout.getDragItemManager().getItems();
                pagerAdapter.change(newSubjects);

                binding.flowLayout.getDragItemManager().clearItems();
                binding.unused.getDragItemManager().clearItems();

                search();

                new Thread(() -> {
                    // store the preference into DB
                    Preference preference;
                    List<Preference> res = preferenceBox.getAll();
                    if (res != null && res.size() > 0)
                        preference = res.get(0);
                    else
                        preference = new Preference();
                    preference.setSubjects(new ArrayList<>(newSubjects));
                    preferenceBox.put(preference);
                    // upload
                    Backend.getInst().uploadPreferences(preference, null);
                }).start();
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
        Log.d("MYDEBUG", pos + " " +pagerAdapter.getCount());
        ((EntityListFragment)pagerAdapter.getItem(pos)).search(getQuery(), acTextView);
    }
}