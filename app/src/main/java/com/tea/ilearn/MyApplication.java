package com.tea.ilearn;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.tea.ilearn.model.Preference;
import com.tea.ilearn.net.APIRequest;
import com.tea.ilearn.utils.ObjectBox;

import java.util.List;

import io.objectbox.Box;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ObjectBox.init(this);
        APIRequest.setContext(getApplicationContext());

        boolean isDark = true;
        Box<Preference> preferenceBox = ObjectBox.get().boxFor(Preference.class);
        // load preference from DB
        List<Preference> res = preferenceBox.getAll();
        if (res != null && res.size() > 0)
            isDark = res.get(0).isDark();
        else {
            Preference preference = new Preference();
            preferenceBox.put(preference);
            isDark = preference.isDark();
        }
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
