package com.tea.ilearn.activity.entity_detail;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class FixPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragmentList;

    public FixPagerAdapter(FragmentManager fm, String name, String category, String subject, String uri, ArrayList<String> categories) {
        super(fm);
        fragmentList = new ArrayList<>();
        fragmentList.add(new PropertyListFragment());
        fragmentList.add(new RelationListFragment(name, category, subject, uri, categories));
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Nullable @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) return "属性";
        if (position == 1) return "关系";
        return "";
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
}
