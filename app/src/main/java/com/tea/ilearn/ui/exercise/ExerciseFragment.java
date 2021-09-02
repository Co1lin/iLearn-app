package com.tea.ilearn.ui.exercise;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.databinding.FragmentExerciseBinding;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class ExerciseFragment extends Fragment {
    private FragmentExerciseBinding binding;

    private RecyclerView mInfoRecycler;
    private InfoListAdapter mInfoAdapter;
    private CountDownLatch loadLatch = new CountDownLatch(0);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExerciseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        binding.temp.setOnClickListener($ -> {
//            Intent intent = new Intent(root.getContext(), ExerciseListActivity.class);
//            intent.setAction(Intent.ACTION_SEARCH);
//            intent.putExtra("name", "蛋白质");
//            intent.putExtra("subject", "biology");
//            root.getContext().startActivity(intent);
//        });

        mInfoRecycler = binding.exerciseRecycler;
        mInfoAdapter = new InfoListAdapter(root.getContext(), new ArrayList<Info>());
        mInfoRecycler.setLayoutManager(new LinearLayoutManager(root.getContext()));
        mInfoRecycler.setAdapter(mInfoAdapter);

        getBatch();

        return root;
    }

    private void getBatch() {
        if (loadLatch.getCount() == 0) {
            mInfoAdapter.clear();
            int initNum = 5;
            loadLatch = new CountDownLatch(initNum);
            binding.progressCircular.setVisibility(View.VISIBLE);
            // TODO
        }
    }
}
