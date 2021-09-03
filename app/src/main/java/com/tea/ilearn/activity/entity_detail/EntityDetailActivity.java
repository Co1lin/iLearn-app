package com.tea.ilearn.activity.entity_detail;

import static java.lang.Thread.sleep;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.activity.exercise_list.ExerciseListActivity;
import com.tea.ilearn.databinding.ActivityEntityDetailBinding;
import com.tea.ilearn.model.Category;
import com.tea.ilearn.model.Category_;
import com.tea.ilearn.net.edukg.EduKG;
import com.tea.ilearn.net.edukg.EduKGEntityDetail;
import com.tea.ilearn.net.edukg.EduKGEntityDetail_;
import com.tea.ilearn.net.edukg.EduKGProperty;
import com.tea.ilearn.net.edukg.EduKGRelation;
import com.tea.ilearn.utils.ObjectBox;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.Query;

public class EntityDetailActivity extends AppCompatActivity {
    private ActivityEntityDetailBinding binding;
    private RecyclerView mRelationRecycler, mPropertyRecycler;
    private RelationListAdapter mRelationAdapter, mPropertyAdapter;
    private EduKGEntityDetail entityDetail = new EduKGEntityDetail();
    Box<EduKGEntityDetail> entityBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEntityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mPropertyRecycler = binding.propertyRecycler;
        mPropertyAdapter = new RelationListAdapter(binding.getRoot().getContext(), new ArrayList<>());
        mPropertyRecycler.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        mPropertyRecycler.setAdapter(mPropertyAdapter);

        mRelationRecycler = binding.relationRecycler;
        mRelationAdapter = new RelationListAdapter(binding.getRoot().getContext(), new ArrayList<>());
        mRelationRecycler.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        mRelationRecycler.setAdapter(mRelationAdapter);

        mPropertyRecycler.setNestedScrollingEnabled(false);
        mRelationRecycler.setNestedScrollingEnabled(false);

        binding.hide.setOnClickListener($ -> {
            finish();
        });

        entityBox = ObjectBox.get().boxFor(EduKGEntityDetail.class);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String name = intent.getStringExtra("name");
            String category = intent.getStringExtra("category");
            String subject = intent.getStringExtra("subject");
            String uri = intent.getStringExtra("id");

            binding.entityName.setText(name);
            binding.entityCategory.setText(category);
            binding.entitySubject.setText(subject);

            binding.star.setOnCheckedChangeListener((btn, checked) -> {
                if (binding.star.isChecked())
                    Toast.makeText(binding.getRoot().getContext(), "收藏成功", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(binding.getRoot().getContext(), "已取消收藏", Toast.LENGTH_SHORT).show();
                new Thread(() -> {
                    getEntityDetail(uri);
                    entityDetail.setStared(binding.star.isChecked());
                    entityBox.put(entityDetail);
                }).start();
            });
            getEntityDetail(uri);
            entityDetail.setViewed(true);
            binding.star.setChecked(entityDetail.isStared());
            entityBox.put(entityDetail);

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
                StaticHandler handler = new StaticHandler(binding.entityDescription, mRelationAdapter, mPropertyAdapter, subject, category, entityBox);
                EduKG.getInst().getEntityDetails(subject, name, handler);
                // TODO save to database (including the loaded status)
            }
            else {
                // TODO load from database (star, properties, relations)
            }
        }
    }

    private EduKGEntityDetail getEntityDetail(String uri) {
        synchronized (entityDetail) {
            while (entityDetail.getUri() == null) {
                Query<EduKGEntityDetail> query = entityBox.query()
                        .equal(EduKGEntityDetail_.uri, uri).build();
                List<EduKGEntityDetail> entitiesRes = query.find();
                query.close();
                if (entitiesRes == null || entitiesRes.size() == 0) {
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {}
                } else {
                    entityDetail = entitiesRes.get(0);
                    break;
                }
            }
            return entityDetail;
        }
    }

    class StaticHandler extends Handler {
        private TextView entityDescription;
        private RelationListAdapter mRelationAdapter;
        private RelationListAdapter mPropertyAdapter;
        private Box<EduKGEntityDetail> entityBox;
        String subject, category;

        StaticHandler(TextView entityDescription, RelationListAdapter mRelationAdapter,
                      RelationListAdapter mPropertyAdapter, String subject, String category,
                      Box<EduKGEntityDetail> entityBox) {
            this.entityDescription = entityDescription;
            this.mRelationAdapter = mRelationAdapter;
            this.mPropertyAdapter = mPropertyAdapter;
            this.subject = subject;
            this.category = category;
            this.entityBox = entityBox;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            EduKGEntityDetail detail = (EduKGEntityDetail) msg.obj;
            entityDescription.setText("实体描述仍在标注中...");
            if (detail != null) {
                if (detail.getRelations() != null) {
                    for (EduKGRelation r : detail.getRelations())
                        mRelationAdapter.add(new Relation(r.getPredicateLabel(), r.getObjectLabel(), r.getDirection(), subject, category, r.getObject()));
                }
                if (detail.getProperties() != null) {
                    for (EduKGProperty p : detail.getProperties()) {
                        if (p.getPredicateLabel().equals("描述"))
                            entityDescription.setText("实体描述: "+p.getObject());
                        else
                            mPropertyAdapter.add(new Relation(p.getPredicateLabel(), p.getObject(), 2));
                    }
                }

                new Thread(() -> {
                    // store info to DB for offline loading
                    getEntityDetail(detail.getUri());
                    if (detail.getRelations() != null && detail.getRelations().size() > 0) {
                        entityDetail.setRelations(detail.getRelations());
                        // TODO colin: store entities in relations

                    }
                    if (detail.getProperties() != null && detail.getProperties().size() > 0) {
                        entityDetail.setProperties(detail.getProperties());
                    }
                    // store categories
                    if (entityDetail.getCategoriesBuf() != null && entityDetail.getCategoriesBuf().size() > 0) {
                        Box<Category> categoryBox = ObjectBox.get().boxFor(Category.class);
                        for (String categoryName : entityDetail.getCategoriesBuf()) {
                            Query<Category> query = categoryBox.query()
                                    .equal(Category_.name, categoryName).build();
                            List<Category> categoryRes = query.find();
                            query.close();
                            Category category;
                            if (categoryRes == null || categoryRes.size() == 0)
                                category = new Category().setName(categoryName);
                            else
                                category = categoryRes.get(0);
                            category.increaseNum();
                            entityDetail.categories.add(category);
                        }
                    }
                    entityBox.put(entityDetail);
                }).start();
            } // end if
        }

    }
}