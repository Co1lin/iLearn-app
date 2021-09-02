package com.tea.ilearn.activity.entity_detail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.activity.exercise_list.ExerciseListActivity;
import com.tea.ilearn.databinding.ActivityEntityDetailBinding;
import com.tea.ilearn.net.edukg.EduKG;
import com.tea.ilearn.net.edukg.EduKGEntityDetail;
import com.tea.ilearn.net.edukg.EduKGProperty;
import com.tea.ilearn.net.edukg.EduKGRelation;

import java.util.ArrayList;

public class EntityDetailActivity extends AppCompatActivity {
    private ActivityEntityDetailBinding binding;
    private RecyclerView mRelationRecycler, mPropertyRecycler;
    private RelationListAdapter mRelationAdapter, mPropertyAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEntityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mPropertyRecycler = binding.propertyRecycler;
        mPropertyAdapter = new RelationListAdapter(binding.getRoot().getContext(), new ArrayList<Relation>());
        mPropertyRecycler.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        mPropertyRecycler.setAdapter(mPropertyAdapter);

        mRelationRecycler = binding.relationRecycler;
        mRelationAdapter = new RelationListAdapter(binding.getRoot().getContext(), new ArrayList<Relation>());
        mRelationRecycler.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        mRelationRecycler.setAdapter(mRelationAdapter);

        mPropertyRecycler.setNestedScrollingEnabled(false);
        mRelationRecycler.setNestedScrollingEnabled(false);

        binding.hide.setOnClickListener($ -> {
            finish();
        });

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String name = intent.getStringExtra("name");
            String id = intent.getStringExtra("id");
            String category = intent.getStringExtra("category");
            String subject = intent.getStringExtra("subject");

            binding.entityName.setText(name);
            binding.entityCategory.setText(category);
            binding.entitySubject.setText(subject);

            binding.star.setOnCheckedChangeListener((btn, checked) -> {
                // TODO save current "star" status in database
            });
            binding.share.setOnClickListener($ -> {
                // TODO share related sdk
            });
            binding.relatedExercise.setOnClickListener($ -> {
                Intent inten = new Intent (binding.getRoot().getContext(), ExerciseListActivity.class);
                inten.setAction(Intent.ACTION_SEARCH);
                inten.putExtra("name", name);
                inten.putExtra("subject", subject);
                binding.getRoot().getContext().startActivity(inten);
            });

            boolean loaded = false; // TODO get info from database (base on id?)
            if (!loaded) {
                StaticHandler handler = new StaticHandler(binding.entityDescription, mRelationAdapter, mPropertyAdapter);
                EduKG.getInst().getEntityDetails(subject, name, handler);
                // TODO save to database
            }
            else {
                // TODO load from database (star, properties, relations)
            }
        }
    }

    static class StaticHandler extends Handler {
        private TextView entityDescription;
        private RelationListAdapter mRelationAdapter;
        private RelationListAdapter mPropertyAdapter;

        StaticHandler(TextView entityDescription, RelationListAdapter mRelationAdapter, RelationListAdapter mPropertyAdapter) {
            this.entityDescription = entityDescription;
            this.mRelationAdapter = mRelationAdapter;
            this.mPropertyAdapter = mPropertyAdapter;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            EduKGEntityDetail detail = (EduKGEntityDetail) msg.obj;
            if (detail != null) {
                entityDescription.setText("实体描述，这里还不知道放啥"); // TODO
                if (detail.getRelations() != null) {
                    for (EduKGRelation r : detail.getRelations()) {
                        mRelationAdapter.add(new Relation(r.getPredicateLabel(), r.getObjectLabel(), r.getDirection())); // TODO 格式调整
                    }
                }
                if (detail.getProperties() != null) {
                    for (EduKGProperty p : detail.getProperties()) {
                        mPropertyAdapter.add(new Relation(p.getPredicateLabel(), p.getObject(), 2)); // TODO 格式调整
                    }
                }
            } else {
                // TODO empty content in entity detail
            }
        }
    }
}