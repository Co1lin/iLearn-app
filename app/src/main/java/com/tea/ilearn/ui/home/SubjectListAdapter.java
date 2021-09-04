package com.tea.ilearn.ui.home;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class SubjectListAdapter extends FragmentPagerAdapter {
    private List<EntityListFragment> mFragmentList;
    public List<String> subjects;

    public SubjectListAdapter(FragmentManager fm, List<String> subjects, List<EntityListFragment> fragmentList) {
        super(fm);
        this.subjects = subjects;
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return subjects.size();
    }

    @Nullable @Override
    public CharSequence getPageTitle(int position) {
        return subjects.get(position);
    }
}
