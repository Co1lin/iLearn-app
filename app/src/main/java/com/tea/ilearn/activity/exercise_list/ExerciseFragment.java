package com.tea.ilearn.activity.exercise_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.tea.ilearn.databinding.ExerciseCardBinding;

public class ExerciseFragment extends Fragment {
    private ExerciseCardBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ExerciseCardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        return root;
    }
}
