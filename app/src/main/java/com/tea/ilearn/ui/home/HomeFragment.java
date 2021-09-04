package com.tea.ilearn.ui.home;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.tea.ilearn.Constant;
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
    }

    private void search() {
        // TODO  acha
    }
}