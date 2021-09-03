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
import androidx.fragment.app.FragmentManager;
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

        binding.progressCircular.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String name = intent.getStringExtra("name");
            String subject = intent.getStringExtra("subject");
            binding.name.setText(name + "相关习题");
            StaticHandler handler = new StaticHandler(getSupportFragmentManager(), binding.exerciseList, subject, binding.progressCircular, binding.notFound, mWBAPI);
            EduKG.getInst().getProblems(name, handler); // TODO Colin getProblems by course api
        }
    }

    static class StaticHandler extends Handler {
        private FragmentManager fm;
        private ViewPager vp;
        private String subject;
        private View progress, notfound;
        private IWBAPI mWPAPI;

        StaticHandler(FragmentManager fm, ViewPager vp, String subject, View progress, View notfound, IWBAPI WBAPI) {
            this.fm = fm;
            this.vp = vp;
            this.subject = subject;
            this.progress = progress;
            this.notfound = notfound;
            this.mWPAPI = WBAPI;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            List<Problem> problems = (List<Problem>) msg.obj;
            if (problems != null && problems.size() != 0) {
                List<ExerciseFragment> fragments = new ArrayList<>();
                int i = 0;
                for (Problem p : problems) { // TODO these code is slow, acceleration is needed
                    i += 1;
                    ExerciseFragment fragment = new ExerciseFragment(
                            i+"/"+problems.size(),
                            p.getDescription(),
                            p.getChoices(),
                            p.getAnswer(),
                            mWPAPI
                    );
                    fragments.add(fragment);
                }
                ExerciseListAdapter mExerciseAdapter = new ExerciseListAdapter(fm, fragments);
                vp.setOffscreenPageLimit(fragments.size());
                vp.setPageMargin(10);
                vp.setAdapter(mExerciseAdapter);
            } else {
                notfound.setVisibility(View.VISIBLE);
            }
            progress.setVisibility(View.GONE);
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
