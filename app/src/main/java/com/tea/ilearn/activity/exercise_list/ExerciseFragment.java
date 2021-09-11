package com.tea.ilearn.activity.exercise_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.tea.ilearn.R;
import com.tea.ilearn.databinding.ExerciseCardBinding;

public class ExerciseFragment extends Fragment {
    private ExerciseCardBinding binding;
    private String description;
    private String pageNumber;
    private String[] choices;
    private String answer;
    private IWBAPI mWBAPI;
    private boolean examMode;

    public void setPageNumber(String pageNumber) {
        this.pageNumber = pageNumber;
    }

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

//        binding.star.setOnClickListener($ -> {
//            if (binding.star.isChecked()) {
//                Toast.makeText(root.getContext(), "收藏成功", Toast.LENGTH_SHORT).show();
//            }
//            else {
//                Toast.makeText(root.getContext(), "已取消收藏", Toast.LENGTH_SHORT).show();
//            }
//            // save to dababase
//        });
        binding.share.setOnClickListener($ -> {
            doWeiboShare();
        });
        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (!examMode) getScore();
        });

        return root;
    }

    public ExerciseFragment(String pageNumber, String description, String[] choices, String answer, IWBAPI WBAPI, boolean examMode) {
        super();
        this.pageNumber = pageNumber;
        this.description = description;
        this.choices = choices;
        this.answer = answer;
        this.mWBAPI = WBAPI;
        this.examMode = examMode;
    }

    public int getScore() {
        binding.answer.setVisibility(View.VISIBLE);
        binding.radioGroup.setClickable(false);
        binding.radioA.setClickable(false);
        binding.radioB.setClickable(false);
        binding.radioC.setClickable(false);
        binding.radioD.setClickable(false);
        if (answer.equals("A")) binding.radioA.setTextColor(getResources().getColor(R.color.md_green_500));
        if (answer.equals("B")) binding.radioB.setTextColor(getResources().getColor(R.color.md_green_500));
        if (answer.equals("C")) binding.radioC.setTextColor(getResources().getColor(R.color.md_green_500));
        if (answer.equals("D")) binding.radioD.setTextColor(getResources().getColor(R.color.md_green_500));
        if (!answer.equals("A") && binding.radioA.isChecked()) binding.radioA.setTextColor(getResources().getColor(R.color.md_red_500));
        if (!answer.equals("B") && binding.radioB.isChecked()) binding.radioB.setTextColor(getResources().getColor(R.color.md_red_500));
        if (!answer.equals("C") && binding.radioC.isChecked()) binding.radioC.setTextColor(getResources().getColor(R.color.md_red_500));
        if (!answer.equals("D") && binding.radioD.isChecked()) binding.radioD.setTextColor(getResources().getColor(R.color.md_red_500));
        if (answer.equals("A") && binding.radioA.isChecked()) return 1;
        if (answer.equals("B") && binding.radioB.isChecked()) return 1;
        if (answer.equals("C") && binding.radioC.isChecked()) return 1;
        if (answer.equals("D") && binding.radioD.isChecked()) return 1;
        return 0;
    }

    private void doWeiboShare() {
        WeiboMultiMessage message = new WeiboMultiMessage();
        TextObject textObject = new TextObject();
        textObject.text = "#iLearn# 这题出的真好！快来看看你会做吗？\n\n" + description + "\nA." + choices[0] +
                "\nB." + choices[1] + "\nC." + choices[2] + "\nD." + choices[3] + "\n";
        message.textObject = textObject;
        mWBAPI.shareMessage(message, true);
    }
}
