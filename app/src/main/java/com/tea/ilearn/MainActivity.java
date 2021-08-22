package com.tea.ilearn;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tea.ilearn.databinding.ActivityMainBinding;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;
import net.yslibrary.android.keyboardvisibilityevent.Unregistrar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    View fab, bottomAppBar, frame;
    CoordinatorLayout.LayoutParams params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
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

        fab = findViewById(R.id.fab);
        bottomAppBar = findViewById(R.id.bottomAppBar);
        frame = findViewById(R.id.nav_host_fragment_activity_main);
        params = new CoordinatorLayout.LayoutParams(-1, -1);

        final ViewTreeObserver observer= fab.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                params.setMargins(0, 0, 0, fab.getHeight());
                frame.setLayoutParams(params);
            }
        });

        KeyboardVisibilityEvent.setEventListener(
        this,
            new KeyboardVisibilityEventListener() {
                @Override
                public void onVisibilityChanged(boolean isOpen) {
                    if (isOpen) {
                        findViewById(R.id.fab).setVisibility(View.GONE);
                        findViewById(R.id.bottomAppBar).setVisibility(View.GONE);
                    }
                    else {
                        findViewById(R.id.fab).setVisibility(View.VISIBLE);
                        findViewById(R.id.bottomAppBar).setVisibility(View.VISIBLE);
                    }
                }
            }
        );
    }
}