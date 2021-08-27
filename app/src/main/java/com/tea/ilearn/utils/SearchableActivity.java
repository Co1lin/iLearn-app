package com.tea.ilearn.utils;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.app.ListActivity;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.tea.ilearn.R;
import com.tea.ilearn.net.EduKG.EduKG;
import com.tea.ilearn.net.EduKG.EntityDetail;
import com.tea.ilearn.net.EduKG.Problem;

public class SearchableActivity extends Activity {
    private MaterialToolbar topAppBar;
    private boolean star;
    private Activity that;

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
                return false;
            }
        });

        // ===================================================================

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            topAppBar.setTitle(query);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(
                    this, SearchSuggestionProvider.AUTHORITY,
                    SearchSuggestionProvider.MODE
            );
            suggestions.saveRecentQuery(query, null);
            // TODO: clear history for privacy
            // suggestions.clearHistory();
            Log.i("SearchableActivity", "onCreate: got query:" + query);
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