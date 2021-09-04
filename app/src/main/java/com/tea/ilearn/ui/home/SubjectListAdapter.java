package com.tea.ilearn.ui.home;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tea.ilearn.Constant;

import java.util.ArrayList;
import java.util.List;

public class SubjectListAdapter extends FragmentPagerAdapter {
    public List<String> subjects;
    private List<EntityListFragment> fragmentList;

    public SubjectListAdapter(FragmentManager fm, List<String> subjects) {
        super(fm);
        this.subjects = subjects;
        fragmentList = new ArrayList<>();
        for (String subject : subjects) {
            fragmentList.add(new EntityListFragment());
        }
    }

    public void change(List<String> newSubjects) {
        List<EntityListFragment> newFragmentList = new ArrayList<>();
        for (String subj : newSubjects) {
            boolean flag = true;
            for (int k = 0; k < subjects.size(); ++k)
                if (subjects.get(k) == subj) {
                    newFragmentList.add(fragmentList.get(k));
                    flag = false;
                    break;
                }
            if (flag) newFragmentList.add(new EntityListFragment());
        }
        subjects = newSubjects;
        fragmentList = newFragmentList;
        notifyDataSetChanged();
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
}
