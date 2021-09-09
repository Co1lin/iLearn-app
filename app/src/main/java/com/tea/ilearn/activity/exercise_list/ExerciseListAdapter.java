package com.tea.ilearn.activity.exercise_list;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class ExerciseListAdapter extends FragmentPagerAdapter {
    private List<ExerciseFragment> mFragmentList;

    public ExerciseListAdapter(FragmentManager fm) {
        super(fm);
    }

    public void setList(List<ExerciseFragment> fragmentList) {
        mFragmentList = fragmentList;
    }


    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }
}
