package com.tea.ilearn.activity.entity_detail;

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
import com.tea.ilearn.databinding.ActivityEntityDetailBinding;
import com.tea.ilearn.databinding.ActivitySearchableBinding;
import com.tea.ilearn.net.edukg.EduKG;
import com.tea.ilearn.net.edukg.EntityDetail;
import com.tea.ilearn.ui.home.InfoListAdapter;

import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity {
    private ActivityEntityDetailBinding binding;
    private RecyclerView mInfoRecycler, mRelationRecycler, mPropertyRecycler;
    private InfoListAdapter mInfoAdapter;
    private RelationListAdapter mRelationAdapter, mPropertyAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEntityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // ===================================================================

        mRelationRecycler = binding.relationRecycler;
        mRelationAdapter = new RelationListAdapter(binding.getRoot().getContext(), new ArrayList<Relation>());
        mRelationRecycler.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        mRelationRecycler.setAdapter(mRelationAdapter);

        mPropertyRecycler = binding.propertyRecycler;
        mPropertyAdapter = new RelationListAdapter(binding.getRoot().getContext(), new ArrayList<Relation>());
        mPropertyRecycler.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        mPropertyRecycler.setAdapter(mPropertyAdapter);

        binding.hide.setOnClickListener($ -> {
            binding.detailPage.setVisibility(View.GONE);
        });
        binding.star.setOnCheckedChangeListener((btn, checked) -> {
            // TODO save current "star" status in database (related to current entity)
        });
        binding.share.setOnClickListener($ -> {
            // TODO share related sdk (related to current entity)
        });
        binding.relatedExercise.setOnClickListener($ -> {
            // TODO related problem (related to current entity)
        });

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            for (String sub : Constant.EduKG.SUBJECTS) {
                StaticHandler handler = new StaticHandler(mInfoAdapter, sub);
                EduKG.getInst().fuzzySearchEntityWithCourse(sub, query, handler);
            }
        }
    }

    static class StaticHandler extends Handler {
        private RelationListAdapter mRelationAdapter;
        private RelationListAdapter mPropertyAdapter;

        StaticHandler(RelationListAdapter mRelationAdapter, RelationListAdapter mPropertyAdapter) {
            this.mPropertyAdapter = mPropertyAdapter;
            this.mRelationAdapter = mRelationAdapter;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            EntityDetail detail = (EntityDetail) msg.obj;
            if (detail != null) {
                if (detail.getRelations() != null) {
                    for (EntityDetail.Relation r : detail.getRelations()) {
                        mRelationAdapter.add(new Relation(r.getPredicateLabel(), r.getObjectLabel(), r.getDirection()));
                    }
                }
                if (detail.getProperties() != null) {
                    for (EntityDetail.Property p : detail.getProperties()) {
                        mPropertyAdapter.add(new Relation(p.getPredicateLabel(), p.getObject(), 2));
                    }
                }
            } else {
                // TODO empty content in entity detail
            }
        }
    }
}