package com.tea.ilearn;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tea.ilearn.databinding.ActivityMainBinding;
import com.tea.ilearn.ui.chatbot.ChatbotFragment;
import com.tea.ilearn.ui.exercise.ExerciseFragment;
import com.tea.ilearn.ui.home.HomeFragment;
import com.tea.ilearn.ui.link.LinkFragment;
import com.tea.ilearn.ui.me.MeFragment;
import com.tea.ilearn.utils.ObjectBox;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ViewPager2 viewPager;
    private FragmentStateAdapter pagerAdapter;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ObjectBox.init(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewPager = binding.pager;
        navView = binding.navView;
        List<Fragment> fragments = new ArrayList<Fragment>() {{
            add(new ExerciseFragment());
            add(new ChatbotFragment());
            add(new HomeFragment());
            add(new LinkFragment());
            add(new MeFragment());
        }};

        pagerAdapter = new ScreenSlidePagerAdapter(this, fragments);
        viewPager.setAdapter(pagerAdapter);
        setDefaultView(2);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                navView.getMenu().getItem(position).setChecked(true);
            }
        });

        viewPager.setOffscreenPageLimit(6);

        navView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case(R.id.navigation_exercise):
                    viewPager.setCurrentItem(0, true);
                    break;
                case(R.id.navigation_chatbot):
                    viewPager.setCurrentItem(1, true);
                    break;
                case(R.id.navigation_home):
                    viewPager.setCurrentItem(2, true);
                    break;
                case(R.id.navigation_link):
                    viewPager.setCurrentItem(3, true);
                    break;
                case(R.id.navigation_me):
                    viewPager.setCurrentItem(4, true);
                    break;
            }
            return true;
        });

        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
            if (isOpen)
                navView.setVisibility(View.GONE);
            else
                navView.setVisibility(View.VISIBLE);
        });
    }

    private void setDefaultView(int position) {
        viewPager.setCurrentItem(position, false);
        navView.getMenu().getItem(position).setChecked(true);
    }

    private class ScreenSlidePagerAdapter extends FragmentStateAdapter {

        private List<Fragment> fragments;

        public ScreenSlidePagerAdapter(FragmentActivity fragmentActivity,
                                       List<Fragment> _fragments) {
            super(fragmentActivity);
            fragments = _fragments;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }

    /**
     * Ref <a href="https://stackoverflow.com/a/28939113">EditText, clear focus on touch outside</a>
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}