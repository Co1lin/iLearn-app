package com.tea.ilearn;

import android.app.Application;

import com.tea.ilearn.net.APIRequest;
import com.tea.ilearn.utils.ObjectBox;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ObjectBox.init(this);
        APIRequest.setContext(getApplicationContext());
    }
}
