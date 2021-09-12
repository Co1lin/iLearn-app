package com.tea.ilearn.utils;

import android.app.Activity;
import android.content.Context;

import com.tea.ilearn.R;
import com.tea.ilearn.model.SearchHistory;
import com.tea.ilearn.model.SearchHistory_;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import per.goweii.actionbarex.common.AutoComplTextView;

public class DB_utils {
    static public void updateACAdapter(Activity activity, Context context, AutoComplTextView acTextView) {
        new Thread(() -> {
            Box<SearchHistory> historyBox = ObjectBox.get().boxFor(SearchHistory.class);
            List<SearchHistory> searchHistories = historyBox.query()
                    .orderDesc(SearchHistory_.timestamp).build().find();
            ArrayList<String> histories = new ArrayList<String>(){{
                for (SearchHistory history : searchHistories)
                    add(history.getKeyword());
            }};
            activity.runOnUiThread(() -> {
                ACAdapter<String> historyAdapter = new ACAdapter<>(
                        context, R.layout.autocompletion_item,
                        R.id.ac_text, R.id.image_button_del, histories, acTextView);
                acTextView.setAdapter(historyAdapter);
            });
        }).start();
    }
}
