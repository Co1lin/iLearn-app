package com.tea.ilearn.activity.exercise_list;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.tea.ilearn.databinding.ActivityExerciseListBinding;
import com.tea.ilearn.net.edukg.EduKG;
import com.tea.ilearn.net.edukg.Problem;

import java.util.ArrayList;
import java.util.List;

public class ExerciseListActivity extends AppCompatActivity {
    private ActivityExerciseListBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityExerciseListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String name = intent.getStringExtra("name");
            String subject = intent.getStringExtra("subject");
            StaticHandler handler = new StaticHandler(getSupportFragmentManager(), binding.exerciseList, subject);
            EduKG.getInst().getProblems(name, handler); // TODO Colin getProblems by course api
        }
    }

    static class StaticHandler extends Handler {
        private FragmentManager fm;
        private ViewPager vp;
        private String subject;

        StaticHandler(FragmentManager fm, ViewPager vp, String subject) {
            this.fm = fm;
            this.vp = vp;
            this.subject = subject;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            List<Problem> problems = (List<Problem>) msg.obj;
            if (problems != null) {
                List<ExerciseFragment> fragments = new ArrayList<>();
                int i = 0;
                for (Problem p : problems) { // TODO these code is slow, acceleration is needed
                    i += 1;
                    ExerciseFragment fragment = new ExerciseFragment(
                            i+"/"+problems.size(),
                            p.getDescription(),
                            p.getChoices(),
                            p.getAnswer()
                    );
                    fragments.add(fragment);
                }
                ExerciseListAdapter mExerciseAdapter = new ExerciseListAdapter(fm, fragments);
                vp.setOffscreenPageLimit(fragments.size());
                vp.setPageMargin(10);
                vp.setAdapter(mExerciseAdapter);
            } else {
                // TODO empty hint (may be a new type of viewholder)
            }
        }
    }
}
