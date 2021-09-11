package com.tea.ilearn.activity.entity_detail;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.tea.ilearn.Constant;
import com.tea.ilearn.activity.exercise_list.ExerciseListActivity;
import com.tea.ilearn.databinding.ActivityEntityDetailBinding;
import com.tea.ilearn.model.UserStatistics;
import com.tea.ilearn.net.backend.Backend;
import com.tea.ilearn.net.edukg.EduKG;
import com.tea.ilearn.net.edukg.EduKGEntityDetail;
import com.tea.ilearn.net.edukg.EduKGEntityDetail_;
import com.tea.ilearn.net.edukg.EduKGProperty;
import com.tea.ilearn.net.edukg.EduKGRelation;
import com.tea.ilearn.utils.ObjectBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.Query;

public class EntityDetailActivity extends AppCompatActivity implements WbShareCallback {
    private ActivityEntityDetailBinding binding;
    private String name, category, subject, uri;
    private ArrayList<String> categories;
    private EduKGEntityDetail detailInDB;
    private Box<EduKGEntityDetail> entityBox;
    private FixPagerAdapter pagerAdapter;

    private synchronized void waitUntilDetailGot() {
        // wait until this entity has been stored into DB
        while (detailInDB == null) {
            Query<EduKGEntityDetail> query = entityBox.query()
                    .equal(EduKGEntityDetail_.uri, uri).build();
            List<EduKGEntityDetail> entitiesRes = query.find();
            query.close();
            if (entitiesRes != null && entitiesRes.size() > 0)
                detailInDB = entitiesRes.get(0);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSDK();

        binding = ActivityEntityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.hide.setOnClickListener($ -> finish());

        entityBox = ObjectBox.get().boxFor(EduKGEntityDetail.class);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            name = intent.getStringExtra("name");
            category = intent.getStringExtra("category");
            subject = intent.getStringExtra("subject");
            uri = intent.getStringExtra("id");
            categories = intent.getStringArrayListExtra("categories");

            pagerAdapter = new FixPagerAdapter(getSupportFragmentManager(), name, category, subject, uri, categories);
            binding.viewPager.setOffscreenPageLimit(Constant.EduKG.SUBJECTS_EN.size());
            binding.viewPager.setAdapter(pagerAdapter);
            binding.subjectTabs.setupWithViewPager(binding.viewPager);

            initDB();

            // UI related listeners' binding associated with the detail entry
            binding.star.setOnClickListener($ -> {
                if (binding.star.isChecked())
                    Toast.makeText(binding.getRoot().getContext(), "收藏成功", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(binding.getRoot().getContext(), "已取消收藏", Toast.LENGTH_SHORT).show();
                new Thread(() -> {
                    waitUntilDetailGot();
                    detailInDB.setStarred(binding.star.isChecked());
                    entityBox.put(detailInDB);
                    uploadEntity();
                }).start();
            });

            binding.entityName.setText(name);
            binding.entityCategory.setText(category);
            binding.entitySubject.setText(subject);

            binding.share.setOnClickListener($ -> doWeiboShare());
            binding.relatedExercise.setOnClickListener($ -> {
                Intent anotherIntent = new Intent (binding.getRoot().getContext(), ExerciseListActivity.class);
                anotherIntent.setAction(Intent.ACTION_SEARCH);
                anotherIntent.putExtra("name", name);
                anotherIntent.putExtra("subject", subject);
                binding.getRoot().getContext().startActivity(anotherIntent);
            });

            binding.progressCircular.setVisibility(View.VISIBLE);
            boolean loaded = false; // TODO get info from database (base on id?)
            if (!loaded) {
                StaticHandler handler = new StaticHandler(binding, pagerAdapter);
                EduKG.getInst().getEntityDetails(subject, name, handler);
                // TODO save to database (including the loaded status)
            }
            else {
                // TODO load from database (star, properties, relations)
            }
        }
    }

    class StaticHandler extends Handler {
        private ActivityEntityDetailBinding binding;
        private FixPagerAdapter pagerAdapter;

        StaticHandler(ActivityEntityDetailBinding binding, FixPagerAdapter pagerAdapter) {
            this.binding = binding;
            this.pagerAdapter = pagerAdapter;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            binding.progressCircular.setVisibility(View.GONE);
            ArrayList<EduKGRelation> relations = null;
            ArrayList<EduKGProperty> properties = null;
            if (msg.what == 0) {
                EduKGEntityDetail detailFromNet = (EduKGEntityDetail) msg.obj;
                if (detailFromNet != null) {
                    relations = detailFromNet.getRelations();
                    properties = detailFromNet.getProperties();

                    ArrayList<EduKGRelation> finalRelations = relations;
                    ArrayList<EduKGProperty> finalProperties = properties;
                    new Thread(() -> {  // update relations and properties
                        waitUntilDetailGot();
                        detailInDB.setRelations(finalRelations)
                                .setProperties(finalProperties);
                        entityBox.put(detailInDB);
                    }).start();
                }
            } else { // msg.what = 1
                // TODO load from database and display offline loading hint
                // TODO and set to local variable relations and properties (mentioned above) (use below)
            }

            // ==== fill in ui ====

            if (properties != null) {
                ((PropertyListFragment)pagerAdapter.getItem(0)).set(properties);
            }
            if (relations != null) {
                ((RelationListFragment)pagerAdapter.getItem(1)).set(relations);
            }
        }
    }

    private void initDB() {
        new Thread(() -> {  // detailInDB must not be null after this thread finishes
            // query the entity from DB
            Query<EduKGEntityDetail> query = entityBox.query()
                    .equal(EduKGEntityDetail_.uri, uri).build();
            List<EduKGEntityDetail> entitiesRes = query.find();
            query.close();
            EduKGEntityDetail detailTemp;
            if (entitiesRes != null && entitiesRes.size() > 0) {
                // already exists (also already viewed), update status of starred
                detailTemp = entitiesRes.get(0);
                runOnUiThread(() -> binding.star.setChecked(detailTemp.isStarred()));
            }
            else // new viewed entity, store to DB
                detailTemp = new EduKGEntityDetail().setViewed(true);
            detailTemp.setCategory(category)
                    .setCategory(categories)
                    .setSubject(subject)
                    .setLabel(name)
                    .setUri(uri);
            detailInDB = detailTemp;
            entityBox.put(detailInDB);
        }).start();
        new Thread(() -> {  // update statistics
            Box<UserStatistics> statisticsBox = ObjectBox.get().boxFor(UserStatistics.class);
            List<UserStatistics> statisticsRes = statisticsBox.getAll();
            UserStatistics statistics;
            if (statisticsRes != null && statisticsRes.size() > 0)
                statistics = statisticsRes.get(0).increaseLastNum();
            else
                statistics = new UserStatistics().setFirstDate(LocalDate.now().minusDays(6).toString());
            statisticsBox.put(statistics);
            Backend.getInst().uploadUserStatistics(statistics, null);

        }).start();
        new Thread(() -> uploadEntity()).start();
    }

    private void uploadEntity() {
        waitUntilDetailGot();
        Backend.getInst().uploadEntity(detailInDB, null);
    }

    // ==========================================================================

    private IWBAPI mWBAPI;

    private void initSDK() {
        AuthInfo authInfo = new AuthInfo(this, "83638447", "", "");
        mWBAPI = WBAPIFactory.createWBAPI(this);
        mWBAPI.registerApp(this, authInfo);
    }

    @Override
    public void onComplete() {
        Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(UiError error) {
        Toast.makeText(this, "分享失败:" + error.errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {
        Toast.makeText(this, "分享取消", Toast.LENGTH_SHORT).show();
    }

    private void doWeiboShare() {
        WeiboMultiMessage message = new WeiboMultiMessage();
        TextObject textObject = new TextObject();
        textObject.text = "#iLearn# 我今天在iLearn学习了”"+binding.entityName.getText().toString()+"“这个实体，学到了很多东西，快来加入iLearn与我一起学习！";
        message.textObject = textObject;
        mWBAPI.shareMessage(message, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mWBAPI != null) {
            mWBAPI.doResultIntent(data, this);
        }
    }
}