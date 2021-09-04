package com.tea.ilearn.utils;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;

import com.tea.ilearn.R;
import com.tea.ilearn.model.SearchHistory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.objectbox.Box;
import per.goweii.actionbarex.common.AutoComplTextView;

public class DB_utils {
    static public void updateACAdapter(FragmentActivity activity, Context context, AutoComplTextView acTextView) {
        new Thread(() -> {
            Box<SearchHistory> historyBox = ObjectBox.get().boxFor(SearchHistory.class);
            List<SearchHistory> searchHistories = historyBox.getAll();
            ArrayList<String> histories = new ArrayList<>();
            HashMap<String, Boolean> added = new HashMap<>();
            for (SearchHistory searchHistory : searchHistories) {
                String keyword = searchHistory.getKeyword();
                if (added.get(keyword) != null) {
                    histories.add(keyword);
                    added.put(keyword, true);
                }
            }
            activity.runOnUiThread(() -> {
                ACAdapter<String> historyAdapter = new ACAdapter<>(
                        context, R.layout.autocompletion_item,
                        R.id.ac_text, R.id.image_button_del, histories, acTextView);
                acTextView.setAdapter(historyAdapter);
            });
        }).start();
    }
}
