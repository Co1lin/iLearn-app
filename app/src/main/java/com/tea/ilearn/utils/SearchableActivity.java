package com.tea.ilearn.utils;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SearchRecentSuggestions;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tea.ilearn.R;
import com.tea.ilearn.net.EduKG.EduKG;
import com.tea.ilearn.net.EduKG.EntityDetail;
import com.tea.ilearn.net.EduKG.Problem;

import java.util.List;

public class SearchableActivity extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
                    this, SearchSuggestionProvider.AUTHORITY,
                    SearchSuggestionProvider.MODE
            );
            suggestions.saveRecentQuery(query, null);
            // TODO: clear history for privacy
            // suggestions.clearHistory();
            Log.i("SearchableActivity", "onCreate: got query:" + query);
            EduKG.getInst().fuzzySearchEntityWithCourse("chinese", "文章", new StaticHandler());
            EduKG.getInst().getEntityDetails("chinese", "杜甫", new StaticHandler());
            EduKG.getInst().getProblems("细胞", new StaticHandler());
        }
    }

    static class StaticHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.e("search", msg.what + (msg.obj == null ? "" : msg.obj.toString()));
            //Problem prob = ( (List<Problem>) msg.obj ).get(0);

        }
    }
}