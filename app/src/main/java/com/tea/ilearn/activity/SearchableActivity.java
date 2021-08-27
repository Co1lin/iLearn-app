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
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.tea.ilearn.R;
import com.tea.ilearn.net.EduKG.EduKG;
import com.tea.ilearn.ui.chatbot.MessageListAdapter;

import java.util.ArrayList;

public class SearchableActivity extends Activity {
    private MaterialToolbar topAppBar;
    private boolean star;
    private Activity that;
    private RecyclerView mInfoRecycler;
    private InfoListAdapter mInfoAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_searchable);

        that = this;

        topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavUtils.navigateUpFromSameTask(that);
            }
        });
        star = false; // TODO load data
        Drawable drawable;
        if (!star) {
            drawable = DrawableCompat.wrap(getDrawable(R.drawable.ic_favorite_border_24));
        } else {
            drawable = DrawableCompat.wrap(getDrawable(R.drawable.ic_favorite_filled_24));
        }
        DrawableCompat.setTint(drawable, ContextCompat.getColor(that,R.color.teal_200));
        topAppBar.getMenu().findItem(R.id.favorite).setIcon(drawable);
        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.favorite) {
                    if (star == false) {
                        Drawable drawable = DrawableCompat.wrap(getDrawable(R.drawable.ic_favorite_filled_24));
                        DrawableCompat.setTint(drawable, ContextCompat.getColor(that,R.color.teal_200));
                        item.setIcon(drawable);
                    }
                    else {
                        Drawable drawable = DrawableCompat.wrap(getDrawable(R.drawable.ic_favorite_border_24));
                        DrawableCompat.setTint(drawable, ContextCompat.getColor(that,R.color.teal_200));
                        item.setIcon(drawable);
                    }
                    star = !star;
                    // TODO save data
                }
                else if (item.getItemId() == R.id.share) {
                    // TODO share sdk support
                }
                return false;
            }
        });

        // ===================================================================

        mInfoRecycler = findViewById(R.id.info_recycler);
        mInfoAdapter = new InfoListAdapter(this, new ArrayList<Info>());
        mInfoRecycler.setLayoutManager(new LinearLayoutManager(this));
        mInfoRecycler.setAdapter(mInfoAdapter);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            topAppBar.setTitle(query);
            mInfoAdapter.add(new Info(0, "李白", "李白字太白，著作有xxx"));
            mInfoAdapter.add(new Info(0, "白鸽", "一种白色的鸟"));

            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
                    this, SearchSuggestionProvider.AUTHORITY,
                    SearchSuggestionProvider.MODE
            ); // TODO reinstall bug?
            suggestions.saveRecentQuery(query, null);
            // suggestions.clearHistory(); // TODO: clear history for privacy

            StaticHandler handler = new StaticHandler();
            //EduKG.getInst().fuzzySearchEntityWithCourse("chinese", "文章", handler);
            //EduKG.getInst().getEntityDetails("chinese", "杜甫", handler);
            //EduKG.getInst().getProblems("细胞", handler);
            EduKG.getInst().fuzzySearchEntityWithAllCourse("鸟", handler);
        }
    }

    static class StaticHandler extends Handler {
        /**
         * Run on UI Thread!
         */
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.e("search", msg.what + (msg.obj == null ? "" : msg.obj.toString()));
        }
    }
}