package com.tea.ilearn.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.Constant;
import com.tea.ilearn.R;
import com.tea.ilearn.databinding.FragmentHomeBinding;
import com.tea.ilearn.net.edukg.EduKG;
import com.tea.ilearn.net.edukg.Entity;
import com.tea.ilearn.utils.ACAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import per.goweii.actionbarex.common.ActionBarSearch;
import per.goweii.actionbarex.common.AutoComplTextView;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ActionBarSearch searchBar;
    private AutoComplTextView acTextView;
    private View loadingBar;
    private View root;

    private RecyclerView mInfoRecycler;
    private InfoListAdapter mInfoAdapter;

    static final ReentrantLock reentrantLock = new ReentrantLock();
    static volatile CountDownLatch searchNumLatch;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        root = binding.getRoot();
        searchBar = binding.searchBar;
        loadingBar = searchBar.getForegroundLayer();
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
        ArrayList<String> COUNTRIES = new ArrayList<>(Arrays.asList("Belgium", "France", "Italy", "Germany", "Spain", "Sp11", "Sp22"));
        ACAdapter<String> historyAdapter = new ACAdapter<>(
                getContext(), R.layout.autocompletion_item,
                R.id.ac_text, R.id.image_button_del, COUNTRIES,
                acTextView);
        acTextView.setAdapter(historyAdapter);
        acTextView.setDropDownAnchor(searchBar.getId());
        acTextView.setThreshold(1); // default 2, minimum 1
        acTextView.setOnFocusChangeListener((view, hasFocus) -> {
            AutoComplTextView acTView = (AutoComplTextView) view;
            if (hasFocus)
                acTView.showDropDown();
        });

        // ================================================================================

        binding.sortName.setOnClickListener(view -> {
            binding.sortCategoryUp.setVisibility(View.VISIBLE);
            binding.sortCategoryDown.setVisibility(View.VISIBLE);
            if (binding.sortNameUp.getVisibility() == View.VISIBLE && binding.sortNameDown.getVisibility() == View.INVISIBLE) {
                binding.sortNameUp.setVisibility(View.INVISIBLE);
                binding.sortNameDown.setVisibility(View.VISIBLE);
                mInfoAdapter.applySortAndFilter(Info::getName, true);
                mInfoRecycler.scrollToPosition(0);
            }
            else {
                binding.sortNameUp.setVisibility(View.VISIBLE);
                binding.sortNameDown.setVisibility(View.INVISIBLE);
                mInfoAdapter.applySortAndFilter(Info::getName, false);
                mInfoRecycler.scrollToPosition(0);
            }
        });

        binding.sortCategory.setOnClickListener(view -> {
            binding.sortNameUp.setVisibility(View.VISIBLE);
            binding.sortNameDown.setVisibility(View.VISIBLE);
            if (binding.sortCategoryUp.getVisibility() == View.VISIBLE && binding.sortCategoryDown.getVisibility() == View.INVISIBLE) {
                binding.sortCategoryUp.setVisibility(View.INVISIBLE);
                binding.sortCategoryDown.setVisibility(View.VISIBLE);
                mInfoAdapter.applySortAndFilter(Info::getCategory, true);
                mInfoRecycler.scrollToPosition(0);
            }
            else {
                binding.sortCategoryUp.setVisibility(View.VISIBLE);
                binding.sortCategoryDown.setVisibility(View.INVISIBLE);
                mInfoAdapter.applySortAndFilter(Info::getCategory, false);
                mInfoRecycler.scrollToPosition(0);
            }
        });

        mInfoRecycler = binding.infoRecycler;
        mInfoAdapter = new InfoListAdapter(root.getContext(), new ArrayList<Info>());
        mInfoRecycler.setLayoutManager(new LinearLayoutManager(root.getContext()));
        mInfoRecycler.setAdapter(mInfoAdapter);

        return root;
    }

    private void search() {
        if (reentrantLock.tryLock()) {
            String query = searchBar.getEditTextView().getText().toString();
            loadingBar.setVisibility(View.VISIBLE);
            mInfoAdapter.clear();
            List<String> subjects = Constant.EduKG.SUBJECTS; // TODO change to tablayout items
            searchNumLatch = new CountDownLatch(subjects.size());
            for (String sub : subjects) {
                StaticHandler handler = new StaticHandler(mInfoAdapter, sub);
                EduKG.getInst().fuzzySearchEntityWithCourse(sub, query, handler);
            }
//            try { // TODO why this stuck handler thread?
//                searchNumLatch.await(); // TODO set timeout
//                loadingBar.setVisibility(View.INVISIBLE);
//            } catch (InterruptedException e) {
//                // TODO deal with timeout (when network unaccessable)
//            }
        } else {
            // TODO 点击过于频繁提示
        }
    }

    static class StaticHandler extends Handler {
        private InfoListAdapter mInfoAdapter;
        private String subject;

        StaticHandler(InfoListAdapter mInfoAdapter, String subject) {
            this.mInfoAdapter = mInfoAdapter;
            this.subject = subject;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            searchNumLatch.countDown();
            super.handleMessage(msg);
            List<Entity> entities = (List<Entity>) msg.obj;
            if (entities != null) {
                for (Entity e : entities) {
                    mInfoAdapter.add(new Info(
                            0,
                            e.getLabel(),
                            subject,
                            false, // TODO from database
                            false, // TODO from database
                            e.getCategory(),
                            e.getUri()
                    ));
                }
            } else {
                // TODO empty hint (may be a new type of viewholder)
            }
        }
    }
}