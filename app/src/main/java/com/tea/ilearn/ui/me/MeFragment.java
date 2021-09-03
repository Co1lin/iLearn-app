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
import com.tea.ilearn.net.backend.Backend;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        List<Integer> value = Arrays.asList(120, 200, 150, 80, 10, 110, 130); // TODO real value here
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

        // TODO: login test
        StaticHandler handler = new StaticHandler();
        Backend.getInst().login("colin", "colin", handler);

        return root;
    }

    static class StaticHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.i("me", String.valueOf(msg.what));
        }
    }
}
// TODO left padding of the text
// TODO issue in dark mode