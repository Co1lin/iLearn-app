package com.tea.ilearn.ui.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tea.ilearn.activity.exercise_list.ExerciseListActivity;
import com.tea.ilearn.databinding.FragmentExerciseBinding;

public class ExerciseFragment extends Fragment {
    private FragmentExerciseBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentExerciseBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.temp.setOnClickListener($ -> {
            Intent intent = new Intent(root.getContext(), ExerciseListActivity.class);
            intent.setAction(Intent.ACTION_SEARCH);
            intent.putExtra("name", "蛋白质");
            intent.putExtra("subject", "biology");
            root.getContext().startActivity(intent);
        });
        return root;
    }
}