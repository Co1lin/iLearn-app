package com.tea.ilearn.ui.me;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.tea.ilearn.R;
import com.tea.ilearn.activity.account.SigninActivity;
import com.tea.ilearn.databinding.FragmentMeBinding;
import com.tea.ilearn.model.Account;
import com.tea.ilearn.model.SearchHistory;
import com.tea.ilearn.model.UserStatistics;
import com.tea.ilearn.net.backend.Backend;
import com.tea.ilearn.utils.ObjectBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.objectbox.Box;

public class MeFragment extends Fragment {
    private FragmentMeBinding binding;
    private int numEgg = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.profile.setOnClickListener($ -> {
            Intent intent = new Intent(root.getContext(), SigninActivity.class);
            root.getContext().startActivity(intent);
        });

        binding.darkModeSwitch.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        binding.appVersion.setOnClickListener($ -> {
            numEgg += 1;
            if (numEgg == 5) {
                numEgg = 0;
                RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(5000);
                rotate.setInterpolator(new LinearInterpolator());
                binding.appVersion.startAnimation(rotate);
            }
        });

        // =======================================================================

        LineChart lineChart = binding.lineChart;
        List<String> key = Arrays.asList("周一", "周二", "周三", "周四", "周五", "周六", "周日");
        List<Integer> value = Arrays.asList(120, 200, 150, 80, 10, 110, 130); // TODO colin: true value here
        List<Entry> entries = new ArrayList<Entry>();
        for (int i = 0; i < key.size(); ++i) {
            entries.add(new Entry(i, value.get(i)));
        }

        LineDataSet dataset = new LineDataSet(entries, "本周学习点"); // add entries to dataset
        TypedValue typedValue = new TypedValue();
        binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
        int color = ContextCompat.getColor(binding.getRoot().getContext(), typedValue.resourceId);
        dataset.setValueTextSize(10);
        dataset.setValueTextColor(color);
        dataset.setDrawFilled(true);
        dataset.setFillDrawable(ContextCompat.getDrawable(root.getContext(), R.drawable.gradient_fill));
        lineChart.setClipValuesToContent(false);
        lineChart.getXAxis().setTextColor(color);
        typedValue = new TypedValue();
        binding.getRoot().getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        color = ContextCompat.getColor(binding.getRoot().getContext(), typedValue.resourceId);
        dataset.setColor(color);

        LineData lineData = new LineData(dataset);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(false);
        lineChart.setScaleXEnabled(false);
        lineChart.setScaleYEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(key));
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.setData(lineData);
        lineChart.invalidate();

        // TODO: register and login
        LoginHandler loginHandler = new LoginHandler();
        RegisterHandler registerHandler = new RegisterHandler();
        //Backend.getInst().register("coln@lin.sdf", "dfkkjkghk0j", "olin", registerHandler);
        Backend.getInst().login("colin", "colin", loginHandler);

        return root;
    }

    static protected void initUser() {
        Box<UserStatistics> statisticsBox = ObjectBox.get().boxFor(UserStatistics.class);
        statisticsBox.removeAll();
        UserStatistics statistics = new UserStatistics()
                .setFirstDate(LocalDate.now().toString())
                .setEntitiesViewed(new ArrayList<>(
                        Collections.nCopies(7, 0)
                ));
        statisticsBox.put(statistics);
        Backend.getInst().uploadUserStatistics(statistics, new InitUserHandler());
    }

    static class RegisterHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.i("MeFragment/RegisterHandler", String.valueOf(msg.what));
            if (msg.what == 0 && msg.obj != null) {
                Account account = (Account) msg.obj;
                initUser();

                // TODO
            }
            else {  // register failed
                if (((AtomicReference<String>) msg.obj).toString().contains("duplicated")) {
                    // TODO
                }
            }
        }
    }

    static class LoginHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.i("MeFragment/LoginHandler", String.valueOf(msg.what));
            if (msg.what == 0 && msg.obj != null) {
                Account account = (Account) msg.obj;
                // TODO
            }
            else {  // register failed
                if (((String) msg.obj).contains("login failed")) {
                    // TODO: incorrect username or password
                }
            }
        }
    }

    static class InitUserHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.i("MeFragment/InitUserHandler", String.valueOf(msg.what));
            if (msg.what == 0 && msg.obj != null) {
                //Account account = (Account) msg.obj;

                // TODO
            }
            else {  // register failed
                if (((String) msg.obj).contains("duplicated")) {
                    // TODO
                }
            }
        }
    }
}
// TODO edit text color issue in dark mode
