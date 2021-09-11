package com.tea.ilearn.activity;

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

        new Thread(() -> {
            Box<EduKGEntityDetail> detailBox = ObjectBox.get().boxFor(EduKGEntityDetail.class);
            List<EduKGEntityDetail> details = detailBox.query()
                    .orderDesc(EduKGEntityDetail_.timestamp).build().find();
            if (details != null && details.size() > 0) {
                details.forEach((entity) -> {
                    AbstractInfo abstractInfo = new AbstractInfo().setKd(0)
                            .setId(entity.getUri())
                            .setSubject(entity.getSubject())
                            .setName(entity.getLabel())
                            .setCategories(entity.getCategories());
                    abstractInfo.setStar(entity.isStarred()).setLoaded(entity.isViewed());
                    runOnUiThread(() -> mAbstractInfoAdapter.add(abstractInfo));
                });
            }
        }).start();

        //StaticHandler handler = new StaticHandler(binding, mAbstractInfoAdapter);
    }

    /*
    static class StaticHandler extends Handler {
        private ActivityHistoryBinding binding;
        private AbstractInfoListAdapter mAbstractInfoAdapter;

        StaticHandler(ActivityHistoryBinding binding, AbstractInfoListAdapter mAbstractInfoAdapter) {
            this.binding = binding;
            this.mAbstractInfoAdapter = mAbstractInfoAdapter;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.i("HistoryActivity/handleMessage", "msg.what: " + msg.what);
            binding.loading.setVisibility(View.INVISIBLE);

            if (msg.what == 0) {
            }
            else { // msg.what = 1
                binding.emptyHint.setVisibility(View.VISIBLE);
            }
        }
    }
    */
}