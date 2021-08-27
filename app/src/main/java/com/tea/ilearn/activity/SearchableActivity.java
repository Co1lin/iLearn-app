package com.tea.ilearn.activity;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.tea.ilearn.R;
import com.tea.ilearn.net.EduKG.EduKG;
import com.tea.ilearn.net.EduKG.Entity;
import com.tea.ilearn.ui.chatbot.MessageListAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchableActivity extends AppCompatActivity {
    private boolean star;
    private AppCompatActivity that;
    private RecyclerView mInfoRecycler;
    private InfoListAdapter mInfoAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_searchable);

        that = this;

        // ===================================================================

        mInfoRecycler = findViewById(R.id.info_recycler);
        mInfoAdapter = new InfoListAdapter(this, new ArrayList<Info>());
        mInfoRecycler.setLayoutManager(new LinearLayoutManager(this));
        mInfoRecycler.setAdapter(mInfoAdapter);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            StaticHandler handler = new StaticHandler(mInfoAdapter);
            EduKG.getInst().fuzzySearchEntityWithAllCourse(query, handler);
            //EduKG.getInst().getProblems("细胞", handler);

//            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
//                    this, SearchSuggestionProvider.AUTHORITY,
//                    SearchSuggestionProvider.MODE
//            ); // TODO reinstall bug?
//            suggestions.saveRecentQuery(query, null);
//            // suggestions.clearHistory(); // TODO: clear history for privacy
        }
    }

    static class StaticHandler extends Handler {
        private InfoListAdapter mInfoAdapter;

        StaticHandler(InfoListAdapter mInfoAdapter) {
            this.mInfoAdapter = mInfoAdapter;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            List<Entity> entities = (List<Entity>) msg.obj;
            if (entities != null) {
                for (Entity e : entities) {
                    mInfoAdapter.add(new Info(0, e.getLabel(), true));
                }
            } else {
                // TODO empty hint
            }
        }
    }
}