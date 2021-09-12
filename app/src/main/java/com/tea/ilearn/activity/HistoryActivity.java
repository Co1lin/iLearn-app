package com.tea.ilearn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.databinding.ActivityHistoryBinding;
import com.tea.ilearn.net.edukg.EduKGEntityDetail;
import com.tea.ilearn.net.edukg.EduKGEntityDetail_;
import com.tea.ilearn.ui.home.AbstractInfo;
import com.tea.ilearn.ui.home.AbstractInfoListAdapter;
import com.tea.ilearn.utils.ObjectBox;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;

public class HistoryActivity extends AppCompatActivity {
    private ActivityHistoryBinding binding;
    private RecyclerView mAbstractInfoRecycler;
    private AbstractInfoListAdapter mAbstractInfoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        mAbstractInfoRecycler = binding.entityRecycler;
        mAbstractInfoAdapter = new AbstractInfoListAdapter(root.getContext(), new ArrayList<>());
        mAbstractInfoRecycler.setLayoutManager(new LinearLayoutManager(root.getContext()));
        mAbstractInfoRecycler.setAdapter(mAbstractInfoAdapter);

        binding.hide.setOnClickListener($ -> finish());

        Intent intent = getIntent();
        boolean star = intent.getBooleanExtra("star", false);
        if (star) binding.name.setText("我的收藏");
        else binding.name.setText("历史记录");

        new Thread(() -> {
            Box<EduKGEntityDetail> detailBox = ObjectBox.get().boxFor(EduKGEntityDetail.class);
            List<EduKGEntityDetail> details = detailBox.query()
                    .orderDesc(EduKGEntityDetail_.timestamp).build().find();
            if (details != null && details.size() > 0) {
                details.forEach((entity) -> {
                    if (!entity.isStarred() && star) return;
                    AbstractInfo abstractInfo = new AbstractInfo().setKd(0)
                            .setId(entity.getUri())
                            .setSubject(entity.getSubject())
                            .setName(entity.getLabel())
                            .setCategories(entity.getCategories());
                    abstractInfo.setStar(entity.isStarred());
                    runOnUiThread(() -> mAbstractInfoAdapter.add(abstractInfo));
                });
            } else {
                binding.emptyHint.setVisibility(View.VISIBLE);
            }
        }).start();
    }
}