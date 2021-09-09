package com.tea.ilearn.activity.exercise_list;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.tea.ilearn.databinding.ActivityExerciseListBinding;
import com.tea.ilearn.net.edukg.EduKG;
import com.tea.ilearn.net.edukg.Problem;

import java.util.ArrayList;
import java.util.List;

public class ExerciseListActivity extends AppCompatActivity implements WbShareCallback {
    private ActivityExerciseListBinding binding;
    private ExerciseListAdapter mExerciseListAdapter;

    private IWBAPI mWBAPI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initSDK();

        binding = ActivityExerciseListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.hide.setOnClickListener($ -> {
            finish();
        });

        binding.submitBtn.setOnClickListener($ -> {
            int sum = 0, total = 0;
            for (int i = 0; i < mExerciseListAdapter.getCount(); ++i) {
                sum += (ExerciseFragment)(mExerciseListAdapter.getItem(i)).getScore();
                total += 1;
            }
        });

        mExerciseListAdapter = new ExerciseListAdapter(getSupportFragmentManager());

        binding.progressCircular.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String name = intent.getStringExtra("name");
            String subject = intent.getStringExtra("subject");
            binding.name.setText(name + "相关习题");
            StaticHandler handler = new StaticHandler(mExerciseListAdapter, subject, binding, mWBAPI);
            EduKG.getInst().getProblems(name, handler);
            binding.submitBtn.setVisibility(View.GONE);
        } else {
            // TODO do exam
        }
    }

    static class StaticHandler extends Handler {
        private ExerciseListAdapter mExerciseAdapter;
        private String subject;
        private ActivityExerciseListBinding binding;
        private IWBAPI mWPAPI;

        StaticHandler(ExerciseListAdapter mExerciseListAdapter, String subject, ActivityExerciseListBinding binding, IWBAPI WBAPI) {
            this.mExerciseAdapter = mExerciseListAdapter;
            this.subject = subject;
            this.binding = binding;
            this.mWPAPI = WBAPI;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            List<Problem> problems = (List<Problem>) msg.obj;
            if (msg.what == 0) {
                if (problems != null && problems.size() != 0) {
                    List<ExerciseFragment> fragments = new ArrayList<>();
                    int numValidProblem = 0;
                    for (Problem p : problems)
                        if (p.getDescription() != null) ++numValidProblem;
                    int i = 0;
                    for (Problem p : problems) {
                        if (p.getDescription() == null) continue;
                        i += 1;
                        ExerciseFragment fragment = new ExerciseFragment(
                                i+"/"+numValidProblem,
                                p.getDescription(),
                                p.getChoices(),
                                p.getAnswer(),
                                mWPAPI
                        );
                        fragments.add(fragment);
                    }
                    mExerciseAdapter.setList(fragments);
                    mExerciseAdapter.notifyDataSetChanged();
                    binding.exerciseList.setOffscreenPageLimit(fragments.size());
                    binding.exerciseList.setPageMargin(10);
                    binding.exerciseList.setAdapter(mExerciseAdapter);
                } else {
                    binding.notFound.setVisibility(View.VISIBLE);
                }
                binding.submitBtn.setVisibility(View.VISIBLE);
            } else { // msg.what = 1
                // TODO load from database
            }
            binding.progressCircular.setVisibility(View.GONE);
        }
    }

    // ==========================================================================

    private void initSDK() {
        AuthInfo authInfo = new AuthInfo(this, "83638447", "", "");
        mWBAPI = WBAPIFactory.createWBAPI(this);
        mWBAPI.registerApp(this, authInfo);
    }

    @Override
    public void onComplete() {
        Toast.makeText(this, "分享成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(UiError error) {
        Toast.makeText(this, "分享失败:" + error.errorMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCancel() {
        Toast.makeText(this, "分享取消", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mWBAPI != null) {
            mWBAPI.doResultIntent(data, this);
        }
    }
}
