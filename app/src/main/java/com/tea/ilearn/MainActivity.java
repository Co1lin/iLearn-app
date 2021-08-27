package com.tea.ilearn;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tea.ilearn.databinding.ActivityMainBinding;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private SearchView searchView;
    private LinearLayout searchBox;
    private RadioButton searchButton;
    private RadioGroup searchGroup;

    View fab, bottomAppBar, frame;
    CoordinatorLayout.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = binding.navView;
        navView.setBackground(null);
        navView.getMenu().getItem(2).setEnabled(false);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_chatbot,
                R.id.navigation_notifications,
                R.id.navigation_test
        ).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = binding.searchView;
        searchBox = binding.searchBox;
        searchButton = binding.searchEntity;
        searchGroup = binding.searchGroup;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        MainActivity that = this;
        searchGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton btn = (RadioButton)searchGroup.findViewById(checkedId);
            }
        });
        searchBox.setVisibility(View.INVISIBLE);
        searchButton.setChecked(true);

        int searchCloseButtonId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) this.searchView.findViewById(searchCloseButtonId);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setQuery("",false);
                searchBox.setVisibility(View.INVISIBLE);
                searchButton.setChecked(true);
            }
        });

        fab = binding.fab;
        bottomAppBar = binding.bottomAppBar;
        frame = findViewById(R.id.nav_host_fragment_activity_main);
        params = new CoordinatorLayout.LayoutParams(-1, -1);

        final ViewTreeObserver observer= fab.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(() -> {
            params.setMargins(0, 0, 0, fab.getHeight());
            frame.setLayoutParams(params);
        });

        KeyboardVisibilityEvent.setEventListener(this, isOpen -> {
                if (isOpen) {
                    fab.setVisibility(View.GONE);
                    bottomAppBar.setVisibility(View.GONE);
                }
                else {
                    fab.setVisibility(View.VISIBLE);
                    bottomAppBar.setVisibility(View.VISIBLE);
                }
            }
        );
    }

    public void doSearch(View v) {
        searchBox.setVisibility(View.VISIBLE);
        searchView.setIconified(false);
    }
}