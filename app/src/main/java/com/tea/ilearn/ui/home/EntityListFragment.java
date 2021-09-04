package com.tea.ilearn.ui.home;

import static java.lang.Thread.sleep;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.databinding.EntityListBinding;
import com.tea.ilearn.model.SearchHistory;
import com.tea.ilearn.model.SearchHistory_;
import com.tea.ilearn.net.edukg.EduKG;
import com.tea.ilearn.net.edukg.EduKGEntityDetail;
import com.tea.ilearn.net.edukg.EduKGEntityDetail_;
import com.tea.ilearn.net.edukg.Entity;
import com.tea.ilearn.utils.DB_utils;
import com.tea.ilearn.utils.ObjectBox;
import com.tea.ilearn.utils.RandChinese;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import io.objectbox.Box;
import io.objectbox.query.Query;
import per.goweii.actionbarex.common.AutoComplTextView;

public class EntityListFragment extends Fragment {
    private EntityListBinding binding;

    private RecyclerView mInfoRecycler;
    private InfoListAdapter mInfoAdapter;
    private CountDownLatch searchSubjectNum = new CountDownLatch(0);
    String subject;

    public EntityListFragment(String subject) {
        super();
        this.subject = subject;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = EntityListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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

        return root;
    }

    public void waitForBinding(String query, AutoComplTextView acTextView) {
        new Thread(()->{
            while (binding == null) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            getActivity().runOnUiThread(() -> {
                search(query, acTextView);
            });
        }).start();
    }

    public void search(String query, AutoComplTextView acTextView) {
        if (query.isEmpty()) {
            int initNum = 5;
            binding.loadingBar.setVisibility(View.VISIBLE);
            searchSubjectNum = new CountDownLatch(initNum);
            for (int i = 0; i < initNum; ++i) {
                char c = RandChinese.gen();
                query = String.valueOf(c);
                StaticHandler handler = new StaticHandler(mInfoAdapter, subject, query, searchSubjectNum, binding.loadingBar, acTextView, false);
                EduKG.getInst().fuzzySearchEntityWithCourse(subject, query, handler);
            }
            binding.emptyHint.setVisibility(View.GONE);
            return;
        }
        if (searchSubjectNum.getCount() == 0) {
            binding.loadingBar.setVisibility(View.VISIBLE);
            mInfoAdapter.clear();
            binding.emptyHint.setVisibility(View.GONE);
            binding.sortCategoryUp.setVisibility(View.VISIBLE);
            binding.sortCategoryDown.setVisibility(View.VISIBLE);
            binding.sortNameUp.setVisibility(View.VISIBLE);
            binding.sortNameDown.setVisibility(View.VISIBLE);
            searchSubjectNum = new CountDownLatch(1);
            StaticHandler handler = new StaticHandler(mInfoAdapter, subject, query, searchSubjectNum, binding.loadingBar, acTextView, true);
            EduKG.getInst().fuzzySearchEntityWithCourse(subject, query, handler);
        }
    }

    class StaticHandler extends Handler {
        private InfoListAdapter mInfoAdapter;
        private View loadingBar;
        private String subject;
        private CountDownLatch expectedNum;
        private String keyword;
        private AutoComplTextView acTextView;
        private boolean empty_hint;

        StaticHandler(InfoListAdapter mInfoAdapter, String subject,
                      String keyword, CountDownLatch _latch, View _loadingBar, AutoComplTextView acTextView, boolean empty_hint) {
            this.mInfoAdapter = mInfoAdapter;
            this.subject = subject;
            this.expectedNum = _latch;
            this.loadingBar = _loadingBar;
            this.keyword = keyword;
            this.acTextView = acTextView;
            this.empty_hint = empty_hint;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.i("HomeFragment/handleMessage", "msg.what: " + msg.what);
            this.expectedNum.countDown();
            if (this.expectedNum.getCount() == 0)
                loadingBar.setVisibility(View.INVISIBLE);

            Box<SearchHistory> historyBox = ObjectBox.get().boxFor(SearchHistory.class);
            Box<EduKGEntityDetail> entityBox = ObjectBox.get().boxFor(EduKGEntityDetail.class);
            Query<SearchHistory> historyQuery = historyBox.query()
                    .equal(SearchHistory_.keyword, keyword).equal(SearchHistory_.subject, subject).build();
            List<SearchHistory> historiesRes = historyQuery.find();
            historyQuery.close();
            SearchHistory history;
            if (historiesRes == null || historiesRes.size() == 0) {
                history = new SearchHistory().setKeyword(keyword).setSubject(subject);
                historyBox.put(history);
            } else {
                history = historiesRes.get(0);
                history.entities.clear(); // TODO colin: check
            }
            DB_utils.updateACAdapter(getActivity(), getContext(), acTextView);

            if (msg.what == 0) {
                List<Entity> entities = (List<Entity>) msg.obj;
                if (entities != null && entities.size() != 0) {
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
                            EduKGEntityDetail detail;
                            if (entitiesRes != null && entitiesRes.size() > 0) {
                                detail = entitiesRes.get(0);
                                // already exists in DB, update UI
                                mInfoAdapter.modify(idx.get(), detail.isStarred(), detail.isViewed());
                            }
                            else // store a new entity to DB
                                detail = new EduKGEntityDetail();
                            // history has been cleared
                            detail.setLabel(e.getLabel())
                                    .setUri(e.getUri())
                                    .setCategory(uriToCategories.get(e.getUri()))
                                    .setSubject(subject);
                            history.entities.add(detail);

                            idx.incrementAndGet();
                            uriToCategories.remove(e.getUri());
                        }
                    } // end for
                    history.entities.applyChangesToDb();
                }
                else {
                    new Thread(() -> {  // store history with no entity
                        SearchHistory emptyHistory = new SearchHistory().setKeyword(keyword).setSubject(subject);
                        historyBox.put(emptyHistory);
                    }).start();
                    if (empty_hint)
                        binding.emptyHint.setVisibility(View.VISIBLE);
                }
            }
            else { // msg.what = 1
                // TODO acha: no network hint
                // TODO acha: load from database
            }
        }
    }
}
