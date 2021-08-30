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
    private String description, pageNumber;
    private String[] choices;
    private String answer;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ExerciseCardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.pageNumber.setText(pageNumber);
        binding.description.setText(description);

        binding.radioA.setText("A. "+choices[0]);
        binding.radioB.setText("B. "+choices[1]);
        binding.radioC.setText("C. "+choices[2]);
        binding.radioD.setText("D. "+choices[3]);

        binding.answer.setText("标准答案: "+answer);

        binding.star.setOnClickListener($ -> {
            // TODO save to dababase
        });
        binding.share.setOnClickListener($ -> {
            // TODO sdk related
        });
        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            binding.answer.setVisibility(View.VISIBLE);
        });
        // TODO differ normal exercise and examination

        return root;
    }

    public ExerciseFragment(String pageNumber, String description, String[] choices, String answer) {
        super();
        this.pageNumber = pageNumber;
        this.description = description;
        this.choices = choices;
        this.answer = answer;
    }
}
