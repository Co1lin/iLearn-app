package com.tea.ilearn.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.databinding.ActivityHistoryBinding;
import com.tea.ilearn.ui.home.AbstractInfo;
import com.tea.ilearn.ui.home.AbstractInfoListAdapter;

import java.util.ArrayList;

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
        mAbstractInfoAdapter = new AbstractInfoListAdapter(root.getContext(), new ArrayList<AbstractInfo>());
        mAbstractInfoRecycler.setLayoutManager(new LinearLayoutManager(root.getContext()));
        mAbstractInfoRecycler.setAdapter(mAbstractInfoAdapter);

        binding.hide.setOnClickListener($ -> {
            finish();
        });

        StaticHandler handler = new StaticHandler(binding, mAbstractInfoAdapter);
        // TODO colin
    }

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
}