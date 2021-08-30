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
                ExerciseListAdapter mExerciseAdapter = new ExerciseListAdapter(fm, new ArrayList<ExerciseFragment>());
                vp.setAdapter(mExerciseAdapter);

//                for (Problem p : problems) {
//                    mExerciseAdapter.add(new Info(
//                            0,
//                            p.getDescription(),
//                            subject,
//                            false, // TODO from database
//                            false, // TODO from database
//                            String.valueOf(p.getId())
//                    ));
//                }
            } else {
                // TODO empty hint (may be a new type of viewholder)
            }
        }
    }
}
