package com.tea.ilearn;

import android.app.SearchManager;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.tea.ilearn.databinding.ActivityMainBinding;
import com.tea.ilearn.net.EduKG;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SearchView searchView;
    private LinearLayout searchBox;
    private RadioButton searchButton;
    private RadioGroup searchGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setBackground(null);
        navView.getMenu().getItem(2).setEnabled(false);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.navigation_test
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = findViewById(R.id.search_view);
        searchBox = findViewById(R.id.search_box);
        searchButton = findViewById(R.id.search_entity);
        searchGroup = findViewById(R.id.search_group);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        MainActivity that = this;
        searchGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                Log.v("MYDEBUG", "inside");
                RadioButton btn = (RadioButton)searchGroup.findViewById(checkedId);
                { // set new checked color
                    int color = that.getResources().getColor(R.color.white);
                    GradientDrawable drawable = (GradientDrawable)btn.getBackground();
//                    drawable.setStroke(3, color);
                    drawable.setColor(color);
                }
                { // unset old checked color
                    int color = that.getResources().getColor(R.color.purple_200);
                    GradientDrawable drawable = (GradientDrawable)searchButton.getBackground();
//                    drawable.setStroke(3, color);
                    drawable.setColor(color);
                }
                searchButton = btn;
            }
        });
        searchBox.setVisibility(View.INVISIBLE);
        Log.v("MYDEBUG", "outside");
        searchButton.setChecked(true);
        { // set new checked color
            int color = that.getResources().getColor(R.color.white);
            GradientDrawable drawable = (GradientDrawable)searchButton.getBackground();
//            drawable.setStroke(3, color);
            drawable.setColor(color);
        }

        int searchCloseButtonId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) this.searchView.findViewById(searchCloseButtonId);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery("",false);
                searchBox.setVisibility(View.INVISIBLE);
                searchButton = findViewById(R.id.search_entity);
                searchButton.setChecked(true);
            }
        });
    }

    public void doSearch(View v) {
        searchBox.setVisibility(View.VISIBLE);
        searchView.setIconified(false);
    }
}