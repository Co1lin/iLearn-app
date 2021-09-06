package com.tea.ilearn.activity.entity_detail;

import android.content.Intent;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.tea.ilearn.R;
import com.tea.ilearn.activity.exercise_list.ExerciseListActivity;
import com.tea.ilearn.databinding.ActivityEntityDetailBinding;
import com.tea.ilearn.model.UserStatistics;
import com.tea.ilearn.net.edukg.EduKG;
import com.tea.ilearn.net.edukg.EduKGEntityDetail;
import com.tea.ilearn.net.edukg.EduKGEntityDetail_;
import com.tea.ilearn.net.edukg.EduKGProperty;
import com.tea.ilearn.net.edukg.EduKGRelation;
import com.tea.ilearn.utils.ObjectBox;

import java.util.ArrayList;
import java.util.List;

import dev.bandb.graphview.AbstractGraphAdapter;
import dev.bandb.graphview.graph.Graph;
import dev.bandb.graphview.graph.Node;
import dev.bandb.graphview.layouts.layered.SugiyamaArrowEdgeDecoration;
import dev.bandb.graphview.layouts.layered.SugiyamaConfiguration;
import dev.bandb.graphview.layouts.layered.SugiyamaLayoutManager;
import io.objectbox.Box;
import io.objectbox.query.Query;

public class EntityDetailActivity extends AppCompatActivity implements WbShareCallback {
    private ActivityEntityDetailBinding binding;
    private RecyclerView mRelationRecycler, mPropertyRecycler;
    private RelationListAdapter mRelationAdapter, mPropertyAdapter;
    private String name, category, subject, uri;
    private ArrayList<String> categories;
    private EduKGEntityDetail detailInDB;
    private Box<EduKGEntityDetail> entityBox;
    private AbstractGraphAdapter graphAdapter;

    private void waitUntilDetailGot() {
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

        binding.hide.setOnClickListener($ -> finish());

        entityBox = ObjectBox.get().boxFor(EduKGEntityDetail.class);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            name = intent.getStringExtra("name");
            category = intent.getStringExtra("category");
            subject = intent.getStringExtra("subject");
            uri = intent.getStringExtra("id");
            categories = intent.getStringArrayListExtra("categories");

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
                StaticHandler handler = new StaticHandler(binding.entityDescription, binding.progressCircular);
                EduKG.getInst().getEntityDetails(subject, name, handler);
                // TODO save to database (including the loaded status)
            }
            else {
                // TODO load from database (star, properties, relations)
            }
        }

        initGraph();
    }

    void initGraph() {
        SugiyamaConfiguration configuration = new SugiyamaConfiguration.Builder()
                .setNodeSeparation(100)
                .setLevelSeparation(100)
                .build();
        binding.graphView.setLayoutManager(new SugiyamaLayoutManager(binding.getRoot().getContext(), configuration));
        binding.graphView.addItemDecoration(new SugiyamaArrowEdgeDecoration());
        Paint edgeStyle = new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedValue typedValue = new TypedValue();
        binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.colorSecondary, typedValue, true);
        int color = ContextCompat.getColor(binding.getRoot().getContext(), typedValue.resourceId);
        edgeStyle.setColor(color);
        edgeStyle.setStrokeWidth(5f);
        edgeStyle.setStyle(Paint.Style.STROKE);
        edgeStyle.setStrokeJoin(Paint.Join.ROUND);
        edgeStyle.setPathEffect(new CornerPathEffect(10f));
        binding.graphView.addItemDecoration(new SugiyamaArrowEdgeDecoration(edgeStyle));

        graphAdapter = new AbstractGraphAdapter<NodeHolder>() {
            @Override
            public NodeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.node, parent, false);
                return new NodeHolder(view);
            }

            @Override
            public void onBindViewHolder(NodeHolder holder, int position) {
                holder.mText.setText(getNodeData(position).toString());
            }
        };
        binding.graphView.setAdapter(graphAdapter);

        Graph graph = new Graph();
        Node node1 = new Node("key");
        Node node2 = new Node("Child 1");
        Node node3 = new Node("Child 2");
        Node node4 = new Node("Father");
        Node node5 = new Node("Mother");
        Node node6 = new Node("GrandPa");
        Node node7 = new Node("GrandMa");

        graph.addEdge(node1, node2);
        graph.addEdge(node1, node3);
        graph.addEdge(node4, node1);
        graph.addEdge(node5, node1);
        graph.addEdge(node6, node1);
        graph.addEdge(node7, node1);
        graphAdapter.submitGraph(graph);
    }

    private class NodeHolder extends RecyclerView.ViewHolder {
        TextView mText;

        NodeHolder(View itemView) {
            super(itemView);

            mText = itemView.findViewById(R.id.text);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getBindingAdapterPosition();
                    Log.v("MYDEBUG", "Clicked on " + graphAdapter.getNodeData(pos).toString());
                    // TODO new intent here, nodes[pos] is clicked
                }
            });
        }
    }

    class StaticHandler extends Handler {
        private TextView entityDescription;
        private View progress;

        StaticHandler(TextView entityDescription, View progress) {
            this.entityDescription = entityDescription;
            this.progress = progress;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            entityDescription.setText("实体描述仍在标注中...");
            progress.setVisibility(View.GONE);
            if (msg.what == 0) {
                EduKGEntityDetail detailFromNet = (EduKGEntityDetail) msg.obj;
                if (detailFromNet != null) {
                    if (detailFromNet.getRelations() != null) {
                        for (EduKGRelation r : detailFromNet.getRelations())
                            mRelationAdapter.add(new Relation(
                                    r.getPredicateLabel(), r.getObjectLabel(), r.getDirection(),
                                    subject, category, categories, r.getObject()));
                    }
                    if (detailFromNet.getProperties() != null) {
                        for (EduKGProperty p : detailFromNet.getProperties()) {
                            if (p.getPredicateLabel().equals("描述"))
                                entityDescription.setText("实体描述: " + p.getObject());
                            else
                                mPropertyAdapter.add(new Relation(p.getPredicateLabel(), p.getObject(), 2));
                        }
                    }

                    new Thread(() -> {
                        waitUntilDetailGot();
                        detailInDB.setRelations(detailFromNet.getRelations())
                                .setProperties(detailFromNet.getProperties());
                        entityBox.put(detailInDB);
                    }).start();
                }
                else {
                    // successful API request, but got no entity detail; display hint
                }
            }
            else { // msg.what = 1
                // TODO load from database and display offline loading hint
            }

            // finish loading entity's relations
            // begin to show relation graph

        }
    }

    private void initDB() {
        new Thread(() -> {  // detailInDB must not be null after this thread finishes
            // query the entity from DB
            Query<EduKGEntityDetail> query = entityBox.query()
                    .equal(EduKGEntityDetail_.uri, uri).build();
            List<EduKGEntityDetail> entitiesRes = query.find();
            query.close();
            if (entitiesRes != null && entitiesRes.size() > 0) {
                // already exists (also already viewed), update status of starred
                detailInDB = entitiesRes.get(0);
                runOnUiThread(() -> binding.star.setChecked(detailInDB.isStarred()));
            }
            else { // new viewed entity, store to DB
                detailInDB = new EduKGEntityDetail()
                        .setCategory(category)
                        .setCategoriesBuf(categories)
                        .setSubject(subject)
                        .setLabel(name)
                        .setUri(uri)
                        .setViewed(true);
                entityBox.put(detailInDB);
            }
        }).start();
        new Thread(() -> {  // update statistics
            Box<UserStatistics> statisticsBox = ObjectBox.get().boxFor(UserStatistics.class);
            List<UserStatistics> statisticsRes = statisticsBox.getAll();
            if (statisticsRes != null && statisticsRes.size() > 0)
                statisticsBox.put(statisticsRes.get(0).increaseLastNum());
        }).start();
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