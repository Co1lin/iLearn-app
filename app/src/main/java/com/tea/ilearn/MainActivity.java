package com.tea.ilearn;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.heaven7.android.dragflowlayout.ClickToDeleteItemListenerImpl;
import com.heaven7.android.dragflowlayout.DragAdapter;
import com.heaven7.android.dragflowlayout.DragFlowLayout;
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
        });

        // ==================================================================

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = binding.searchView;
        searchBox = binding.searchBox;
        searchButton = binding.searchEntity;
        searchGroup = binding.searchGroup;
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        MainActivity that = this;
        searchGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            RadioButton btn = (RadioButton)searchGroup.findViewById(checkedId);
        });
        searchBox.setVisibility(View.INVISIBLE);
        searchButton.setChecked(true);

        int searchCloseButtonId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = (ImageView) this.searchView.findViewById(searchCloseButtonId);
        closeButton.setOnClickListener(view -> {
            searchView.setQuery("",false);
            searchBox.setVisibility(View.INVISIBLE);
            searchButton.setChecked(true);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchBox.setVisibility(View.INVISIBLE);
                // TODO this cannot handle history suggestion.
                return false;
            }
        });

        binding.dragFlowLayout.setOnItemClickListener(new ClickToDeleteItemListenerImpl(R.id.iv_close){
            @Override
            protected void onDeleteSuccess(DragFlowLayout dfl, View child, Object data) {
                //your code
                Log.v("MYDEBUG", "?");
            }
        });

        binding.dragFlowLayout.setDragAdapter(new DragAdapter<String>() {
            @Override  //获取你的item布局Id
            public int getItemLayoutId() {
                return R.layout.chip;
            }
            //绑定对应item的数据
            @Override
            public void onBindData(View itemView, int dragState, String data) {
                itemView.setTag(data);

                ((TextView) itemView.findViewById(R.id.text)).setText(data);
                itemView.findViewById(R.id.iv_close).setVisibility(
                    dragState != DragFlowLayout.DRAG_STATE_IDLE ? View.VISIBLE : View.GONE
                );
            }

            @NonNull
            @Override
            public String getData(View itemView) {
                return (String) itemView.getTag();
            }
        });

        binding.dragFlowLayout.getDragItemManager().addItems("语文", "数学", "数学", "数学", "数学", "数学", "数学", "数学", "数学", "数学");

        binding.editMenu.setOnClickListener(view -> {
            if (binding.editMenu.isChecked()) {
                binding.dragFlowLayout.beginDrag();
            } else {
                binding.dragFlowLayout.finishDrag();
            }
        });

//        binding.dragFlowLayout.setOnDragStateChangeListener(new DragFlowLayout.OnDragStateChangeListener() {
//            @Override
//            public void onDragStateChange(DragFlowLayout dfl, int dragState) {
//                if (dragState != DragFlowLayout.DRAG_STATE_IDLE)
//                    binding.editMenu.setChecked(true);
//            }
//        });
    }

    public void doSearch(View v) {
        searchBox.setVisibility(View.VISIBLE);
        searchView.setIconified(false);
    }
}