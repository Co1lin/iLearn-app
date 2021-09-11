package com.tea.ilearn.ui.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.activity.SearchExamActivity;
import com.tea.ilearn.databinding.FragmentExerciseBinding;

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

        binding.searchExam.setOnClickListener($ -> {
            Intent intent = new Intent(root.getContext(), SearchExamActivity.class);
            startActivity(intent);
        });

        getBatch();

        return root;
    }

    private void getBatch() {
        if (loadLatch.getCount() == 0) {
            int initNum = 1;
            loadLatch = new CountDownLatch(1);
            binding.progressCircular.setVisibility(View.VISIBLE);
        }
    }
}
