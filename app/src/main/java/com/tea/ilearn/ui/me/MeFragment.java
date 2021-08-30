package com.tea.ilearn.ui.me;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.github.abel533.echarts.axis.CategoryAxis;
import com.github.abel533.echarts.axis.ValueAxis;
import com.github.abel533.echarts.code.X;
import com.github.abel533.echarts.json.GsonOption;
import com.github.abel533.echarts.series.Bar;
import com.tea.ilearn.activity.signin.SigninActivity;
import com.tea.ilearn.databinding.FragmentMeBinding;

public class MeFragment extends Fragment {
    private FragmentMeBinding binding;

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

        binding.barChart.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                refreshBarChart();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        return root;
    }


    private void refreshBarChart() {
        GsonOption option = new GsonOption();
        option.xAxis(new CategoryAxis().data("周一", "周二", "周三", "周四", "周五", "周六", "周日"));
        option.yAxis(new ValueAxis());
        option.title().text("本周学习点").x(X.center);

        Bar bar = new Bar();
        bar.itemStyle().normal().label().show(true).position("inside");
        bar.data(120, 200, 150, 80, 70, 110, 130);

        option.series(bar);

        binding.barChart.refreshEchartsWithOption(option);
    }
}
// TODO left padding of the text
// TODO issue in dark mode