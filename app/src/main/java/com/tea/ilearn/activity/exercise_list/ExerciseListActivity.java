package com.tea.ilearn.activity.exercise_list;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;
import com.sina.weibo.sdk.share.WbShareCallback;
import com.tea.ilearn.databinding.ActivityExerciseListBinding;
import com.tea.ilearn.net.edukg.EduKG;
import com.tea.ilearn.net.edukg.Problem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

public class ExerciseListActivity extends AppCompatActivity implements WbShareCallback {
    private ActivityExerciseListBinding binding;
    private ExerciseListAdapter mExerciseListAdapter;
    private CountDownLatch loadLatch = new CountDownLatch(0);
    private Boolean retry;
    private Object lock = new Object();

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
                sum += ((ExerciseFragment)(mExerciseListAdapter.getItem(i))).getScore();
                total += 1;
            }
            binding.score.setText((int)((double)sum/(double)total*100)+"分");
            binding.submitBtn.setVisibility(View.INVISIBLE);
            binding.score.setVisibility(View.VISIBLE);
        });

        mExerciseListAdapter = new ExerciseListAdapter(getSupportFragmentManager());
        binding.exerciseList.setAdapter(mExerciseListAdapter);
        binding.exerciseList.setPageMargin(10);

        binding.progressCircular.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            List<String> names = intent.getStringArrayListExtra("entities");
            List<ExerciseFragment> fragments = new ArrayList<>();
            if (names == null) {
                String name = intent.getStringExtra("name");
                binding.name.setText(name + "相关习题");
                loadLatch = new CountDownLatch(1);
                StaticHandler handler = new StaticHandler(mExerciseListAdapter, binding, mWBAPI, false, loadLatch, fragments, false, lock);
                EduKG.getInst().getProblems(name, handler);
                binding.submitBtn.setVisibility(View.INVISIBLE);
            } else {
                boolean examMode = intent.getBooleanExtra("exam", true);
                if (examMode) {
                    binding.name.setText("专项测试");
                } else {
                    binding.name.setText("试题推荐");
                }
                new Thread(() -> {
                    synchronized (lock) {
                        StaticHandler handler = new StaticHandler(mExerciseListAdapter, binding, mWBAPI, examMode, loadLatch, fragments, retry, lock);
                        for (int i = 0; i < names.size(); i += 5) {
                            int l = i * 5;
                            int r = Math.min((i+1) * 5, names.size());
                            loadLatch = new CountDownLatch(r-l);
                            for (int j = l; j < r; ++j) {
                                retry = (r!=names.size());
                                int finalJ = j;
                                runOnUiThread(() -> EduKG.getInst().getProblems(names.get(finalJ), handler));
                            }
                            Log.e("handleMessage", "before wait");
                            try {
                                lock.wait();
                                Log.e("handleMessage", "after wait");
                            } catch (InterruptedException e) {
                                Log.e("handleMessage", "not wait: " + e.toString());
                                if (!retry) return;
                            }
                        }
                    }
                    Log.d("MYDEBUG", "here");
                }).start();
            }
        }
    }

    static class StaticHandler extends Handler {
        private ExerciseListAdapter mExerciseListAdapter;
        private ActivityExerciseListBinding binding;
        private IWBAPI mWPAPI;
        private boolean examMode;
        private Boolean retry;
        private Object lock;
        private CountDownLatch loadLatch;
        private List<ExerciseFragment> fragments;

        StaticHandler(ExerciseListAdapter mExerciseListAdapter, ActivityExerciseListBinding binding, IWBAPI WBAPI, boolean examMode, CountDownLatch loadLatch, List<ExerciseFragment> fragments, boolean retry, Object lock) {
            this.mExerciseListAdapter = mExerciseListAdapter;
            this.binding = binding;
            this.mWPAPI = WBAPI;
            this.examMode = examMode;
            this.loadLatch = loadLatch;
            this.fragments = fragments;
            this.retry = retry;
            this.lock = lock;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            loadLatch.countDown();
            Log.e("handleMessage", "before");
            synchronized(lock) {}
            Log.e("handleMessage", "after");
            if (msg.what == 0 && msg.obj != null) {
                List<Problem> problems = (List<Problem>) msg.obj;
                if (problems != null && problems.size() != 0) {
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
                                mWPAPI,
                                examMode
                        );
                        fragments.add(fragment);
                    }
                    binding.notFound.setVisibility(View.GONE);
                }
            }
            if (loadLatch.getCount() == 0) {
                if (fragments.size() == 0) {
                    binding.notFound.setVisibility(View.VISIBLE);
                    if (retry) {
                        synchronized (lock) { lock.notify(); }
                        return;
                    }
                }
                else {
                    if (retry) {
                        retry = false;
                        synchronized (lock) { lock.notify(); }
                    }
                    Collections.shuffle(fragments, new Random(System.nanoTime()));
                    if (fragments.size() > 10) {
                        for (int i = fragments.size() - 1; i >= 10; --i) {
                            fragments.remove(i);
                        }
                    }
                    for (int i = 0; i < fragments.size(); ++i) {
                        fragments.get(i).setPageNumber((i+1)+"/"+fragments.size());
                    }
                    mExerciseListAdapter.set(fragments);
                    if (examMode)
                        binding.submitBtn.setVisibility(View.VISIBLE);
                }
                binding.progressCircular.setVisibility(View.GONE);
            }
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
