package com.tea.ilearn.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.github.abel533.echarts.json.GsonOption;

public class EchartsView extends WebView {
    private static final String TAG = EchartsView.class.getSimpleName();

    public EchartsView(Context context) {
        this(context, null);
    }

    public EchartsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EchartsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        loadUrl("file:///android_asset/echarts.html");

        // disable scrolling
        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return (event.getAction() == android.view.MotionEvent.ACTION_MOVE);
            }
        });
    }

    public void refreshEchartsWithOption(GsonOption option) {
        if (option == null) return;
        Log.v("MYDEBUG", "javascript:loadEcharts('"+option.toString()+"')");
        loadUrl("javascript:loadEcharts('" + option.toString() + "')");
    }
}
