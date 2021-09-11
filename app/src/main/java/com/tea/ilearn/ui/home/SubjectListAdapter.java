package com.tea.ilearn.ui.home;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.tea.ilearn.Constant;

import java.util.ArrayList;
import java.util.List;

public class SubjectListAdapter extends FragmentStatePagerAdapter {
    public List<String> subjects;
    private List<EntityListFragment> fragmentList;

    public SubjectListAdapter(FragmentManager fm, List<String> subjects) {
        super(fm);
        this.subjects = subjects;
        fragmentList = new ArrayList<>();
        for (String subj : subjects) {
            fragmentList.add(new EntityListFragment().setSubject(subj));
        }
    }

    public void change(List<String> newSubjects) {
        subjects = newSubjects;
        fragmentList = new ArrayList<>();
        for (String subj : newSubjects) {
            fragmentList.add(new EntityListFragment().setSubject(subj));
        }
        notifyDataSetChanged();
    }

    public List<String> getSubjects() {
        return subjects;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return subjects.size();
    }

    @Nullable @Override
    public CharSequence getPageTitle(int position) {
        return Constant.EduKG.EN_ZH.get(subjects.get(position));
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
