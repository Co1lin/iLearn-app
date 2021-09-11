package com.tea.ilearn.ui.exercise;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.tea.ilearn.activity.SearchExamActivity;
import com.tea.ilearn.activity.exercise_list.ExerciseListActivity;
import com.tea.ilearn.databinding.FragmentExerciseBinding;
import com.tea.ilearn.net.backend.Backend;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
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

        binding.progressCircular.setOnTouchListener((view, event) -> {
            return true;
        });

        binding.searchExam.setOnClickListener($ -> {
            Intent intent = new Intent(root.getContext(), SearchExamActivity.class);
            startActivity(intent);
        });

        binding.suggestExam.setOnClickListener($ -> {
            binding.progressCircular.setVisibility(View.VISIBLE);
            Backend.getInst().getRecommendedEntities(new SuggestionHandler(binding, this));
        });

        return root;
    }

    static class SuggestionHandler extends Handler {
        private FragmentExerciseBinding binding;
        private Fragment that;

        public SuggestionHandler(FragmentExerciseBinding binding, Fragment that) {
            this.binding = binding;
            this.that = that;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            binding.progressCircular.setVisibility(View.GONE);
            if (msg.what == 0 && msg.obj != null) {
                List<String> entities = (List<String>)(msg.obj);
                if (entities.size() == 0) {
                    Toast.makeText(binding.getRoot().getContext(), "暂无推荐，请重试", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (entities.size() > 10) {
                    List<String> cloned = new ArrayList<>(entities);
                    List<String> tmp10 = new ArrayList<>();
                    for (int i = 0; i < 10; ++i) tmp10.add(cloned.get(i));
                    Collections.shuffle(tmp10, new Random(System.nanoTime()));
                    entities = new ArrayList<>(tmp10);
                    for (int i = 10; i < cloned.size(); ++i) entities.add(cloned.get(i));
                }
                Intent intent = new Intent(binding.getRoot().getContext(), ExerciseListActivity.class);
                intent.setAction(Intent.ACTION_SEARCH);
                intent.putStringArrayListExtra("entities", new ArrayList<String>(entities));
                intent.putExtra("exam", false);
                that.startActivity(intent);
            }
            else if (msg.obj.toString().equals("no network connection")) {
                Toast.makeText(binding.getRoot().getContext(), "网络服务不可用", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(binding.getRoot().getContext(), "获取推荐试题失败，请检查登录状态", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
