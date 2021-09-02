package com.tea.ilearn.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
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
import com.tea.ilearn.model.SearchHistory;
import com.tea.ilearn.model.SearchHistory_;
import com.tea.ilearn.net.edukg.EduKG;
import com.tea.ilearn.net.edukg.EduKGEntityDetail;
import com.tea.ilearn.net.edukg.EduKGEntityDetail_;
import com.tea.ilearn.net.edukg.Entity;
import com.tea.ilearn.utils.ACAdapter;
import com.tea.ilearn.utils.ObjectBox;
import com.tea.ilearn.utils.RandChinese;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import io.objectbox.Box;
import io.objectbox.query.Query;
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

    private CountDownLatch searchSubjectNum = new CountDownLatch(0);

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
            }
            else {
                binding.sortNameUp.setVisibility(View.VISIBLE);
                binding.sortNameDown.setVisibility(View.INVISIBLE);
                mInfoAdapter.applySortAndFilter(Info::getName, false);
            }
            mInfoRecycler.scrollToPosition(0);
        });

        binding.sortCategory.setOnClickListener(view -> {
            binding.sortNameUp.setVisibility(View.VISIBLE);
            binding.sortNameDown.setVisibility(View.VISIBLE);
            if (binding.sortCategoryUp.getVisibility() == View.VISIBLE && binding.sortCategoryDown.getVisibility() == View.INVISIBLE) {
                binding.sortCategoryUp.setVisibility(View.INVISIBLE);
                binding.sortCategoryDown.setVisibility(View.VISIBLE);
                mInfoAdapter.applySortAndFilter(Info::getCategory, true);
            }
            else {
                binding.sortCategoryUp.setVisibility(View.VISIBLE);
                binding.sortCategoryDown.setVisibility(View.INVISIBLE);
                mInfoAdapter.applySortAndFilter(Info::getCategory, false);
            }
            mInfoRecycler.scrollToPosition(0);
        });

        mInfoRecycler = binding.infoRecycler;
        mInfoAdapter = new InfoListAdapter(root.getContext(), new ArrayList<Info>());
        mInfoRecycler.setLayoutManager(new LinearLayoutManager(root.getContext()));
        mInfoRecycler.setAdapter(mInfoAdapter);

        initList();

        return root;
    }

    private void initList() {
        int initNum = 0; // TODO 0 for EDUKG FUCK
        List<String> subjects = Constant.EduKG.SUBJECTS; // TODO change to tablayout items
        searchSubjectNum = new CountDownLatch(subjects.size() * initNum);
        for (int i = 0; i < initNum; ++i) {
            char c = RandChinese.gen();
            String query = String.valueOf(c);
            for (String sub : subjects) {
                StaticHandler handler = new StaticHandler(mInfoAdapter, sub, query, searchSubjectNum, loadingBar);
                EduKG.getInst().fuzzySearchEntityWithCourse(sub, query, handler);
            }
        }
    }

    private void search() {
        if (searchSubjectNum.getCount() == 0) {
            String query = searchBar.getEditTextView().getText().toString();
            loadingBar.setVisibility(View.VISIBLE);
            mInfoAdapter.clear();
            binding.sortCategoryUp.setVisibility(View.VISIBLE);
            binding.sortCategoryDown.setVisibility(View.VISIBLE);
            binding.sortNameUp.setVisibility(View.VISIBLE);
            binding.sortNameDown.setVisibility(View.VISIBLE);
            List<String> subjects = Constant.EduKG.SUBJECTS; // TODO change to tablayout items
            searchSubjectNum = new CountDownLatch(subjects.size());
            for (String sub : subjects) {
                StaticHandler handler = new StaticHandler(
                        mInfoAdapter, sub, query, searchSubjectNum, loadingBar);
                EduKG.getInst().fuzzySearchEntityWithCourse(sub, query, handler);
            }
        }
    }

    static class StaticHandler extends Handler {
        private InfoListAdapter mInfoAdapter;
        private View loadingBar;
        private String subject;
        private CountDownLatch expectedNum;
        private String keyword;

        StaticHandler(InfoListAdapter mInfoAdapter, String subject,
                      String keyword, CountDownLatch _latch, View _loadingBar) {
            this.mInfoAdapter = mInfoAdapter;
            this.subject = subject;
            this.expectedNum = _latch;
            this.loadingBar = _loadingBar;
            this.keyword = keyword;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.i("HomeFragment/handleMessage", "msg.what: " + msg.what);
            this.expectedNum.countDown();
            if (this.expectedNum.getCount() == 0)
                loadingBar.setVisibility(View.INVISIBLE);
            if (msg.what == 0 && msg.obj != null) {
                List<Entity> entities = (List<Entity>) msg.obj;

                Box<SearchHistory> historyBox = ObjectBox.get().boxFor(SearchHistory.class);
                Box<EduKGEntityDetail> entityBox = ObjectBox.get().boxFor(EduKGEntityDetail.class);

                Query<SearchHistory> historyQuery = historyBox.query()
                        .equal(SearchHistory_.keyword, keyword).build();
                List<SearchHistory> historiesRes = historyQuery.find();
                historyQuery.close();
                SearchHistory history;
                if (historiesRes == null || historiesRes.size() == 0) {
                    history = new SearchHistory(keyword);
                    historyBox.put(history);
                } else {
                    history = historiesRes.get(0);
                    history.entities.clear(); // TODO colin: check
                }
                if (entities != null) {
                    // remove duplicate entities with different categories
                    HashMap<String, ArrayList<String>> uriToCategories = new HashMap<>();
                    for (Entity e : entities) {
                        ArrayList<String> categories = uriToCategories.getOrDefault(
                                e.getUri(), new ArrayList<>()
                        );
                        categories.add(e.getCategory());
                        uriToCategories.put(e.getUri(), categories);
                    }
                    HashMap<String, ArrayList<String>> notVisited =
                            (HashMap<String, ArrayList<String>>) uriToCategories.clone();
                    // add to adapter to update UI
                    AtomicInteger idx = new AtomicInteger(mInfoAdapter.getItemCount());
                    for (Entity e : entities) {
                        if (notVisited.get(e.getUri()) != null) {
                            mInfoAdapter.add(new Info(
                                    0,
                                    e.getLabel(),
                                    subject,
                                    false, // false by default
                                    false,
                                    e.getUri(),
                                    uriToCategories.get(e.getUri())
                            ));
                            notVisited.remove(e.getUri());
                        }
                    }
                    // store to DB
                    for (Entity e : entities) {
                        if (uriToCategories.get(e.getUri()) != null) {
                            Query<EduKGEntityDetail> query = entityBox.query()
                                    .equal(EduKGEntityDetail_.uri, e.getUri()).build();
                            List<EduKGEntityDetail> entitiesRes = query.find();
                            query.close();
                            if (entitiesRes != null && entitiesRes.size() > 0) {
                                // already exists in DB, update UI
                                EduKGEntityDetail record = entitiesRes.get(0);
                                mInfoAdapter.modify(idx.get(), record.isStared(), record.isViewed());
                            } else {
                                // store a new entity to DB
                                EduKGEntityDetail detail = new EduKGEntityDetail();
                                detail.setLabel(e.getLabel())
                                        .setUri(e.getUri())
                                        .setCategory(uriToCategories.get(e.getUri()))
                                        .setSubject(subject);
                                history.entities.add(detail);
                            }
                            idx.incrementAndGet();
                            uriToCategories.remove(e.getUri());
                        }
                    } // end for
                    history.entities.applyChangesToDb();
                } else {
                    // TODO empty UI
                    new Thread(() -> {  // store history with no entity
                        SearchHistory emptyHistory = new SearchHistory(keyword);
                        historyBox.put(emptyHistory);
                    }).start();
                }
            }
            else { // msg.what = 1

            }
        }
    }
}