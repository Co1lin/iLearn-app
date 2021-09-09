package com.tea.ilearn.activity.exercise_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.tea.ilearn.databinding.ExerciseCardBinding;

public class ExerciseFragment extends Fragment {
    private ExerciseCardBinding binding;
    private String description, pageNumber;
    private String[] choices;
    private String answer;
    private IWBAPI mWBAPI;

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
            if (binding.star.isChecked()) {
                Toast.makeText(root.getContext(), "收藏成功", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(root.getContext(), "已取消收藏", Toast.LENGTH_SHORT).show();
            }
            // TODO save to dababase
        });
        binding.share.setOnClickListener($ -> {
            doWeiboShare();
        });
        binding.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            binding.answer.setVisibility(View.VISIBLE);
        });
        // TODO acha: differ normal exercise and examination

        return root;
    }

    public ExerciseFragment(String pageNumber, String description, String[] choices, String answer, IWBAPI WBAPI) {
        super();
        this.pageNumber = pageNumber;
        this.description = description;
        this.choices = choices;
        this.answer = answer;
        this.mWBAPI = WBAPI;
    }

    public int getScore() {
        binding.answer.setVisibility(View.VISIBLE);
        binding.radioGroup.setClickable(false);
        return 0;
    }


    private void doWeiboShare() {
        WeiboMultiMessage message = new WeiboMultiMessage();
        TextObject textObject = new TextObject();
        textObject.text = "#iLearn# 这题出的真好！快来看看你会做吗？"+description+" A."+choices[0]+" B."+choices[1]+" C."+choices[2]+" D."+choices[3];
        message.textObject = textObject;
        mWBAPI.shareMessage(message, true);
    }
}
