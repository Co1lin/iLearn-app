package com.tea.ilearn.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.Constant;
import com.tea.ilearn.R;
import com.tea.ilearn.databinding.ActivitySearchableBinding;
import com.tea.ilearn.net.EduKG.EduKG;
import com.tea.ilearn.net.EduKG.Entity;

import java.util.ArrayList;
import java.util.List;

public class SearchableActivity extends AppCompatActivity {
    private ActivitySearchableBinding binding;
    private RecyclerView mInfoRecycler;
    private InfoListAdapter mInfoAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySearchableBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        });

        // ===================================================================

        mInfoRecycler = findViewById(R.id.info_recycler);
        mInfoAdapter = new InfoListAdapter(this, new ArrayList<Info>());
        mInfoRecycler.setLayoutManager(new LinearLayoutManager(this));
        mInfoRecycler.setAdapter(mInfoAdapter);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            for (String sub : Constant.EduKG.SUBJECTS) {
                StaticHandler handler = new StaticHandler(mInfoAdapter, sub);
                EduKG.getInst().fuzzySearchEntityWithCourse(sub, query, handler);
            }

//            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
//                    this, SearchSuggestionProvider.AUTHORITY,
//                    SearchSuggestionProvider.MODE
//            ); // TODO reinstall bug?
//            suggestions.saveRecentQuery(query, null);
//            // suggestions.clearHistory(); // TODO: clear history for privacy
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
            super.handleMessage(msg);
            List<Entity> entities = (List<Entity>) msg.obj;
            if (entities != null) {
                for (Entity e : entities) {
                    mInfoAdapter.add(new Info(
                            0,
                            e.getLabel(),
                            subject,
                            true,
                            false,
                            e.getCategory()
                    ));
                }
            } else {
                // TODO empty hint
            }
        }
    }
}