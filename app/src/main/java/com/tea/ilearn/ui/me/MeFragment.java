package com.tea.ilearn.ui.me;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.gson.Gson;
import com.tea.ilearn.R;
import com.tea.ilearn.activity.account.SigninActivity;
import com.tea.ilearn.databinding.FragmentMeBinding;
import com.tea.ilearn.model.Account;
import com.tea.ilearn.model.UserStatistics;
import com.tea.ilearn.net.backend.Backend;
import com.tea.ilearn.utils.ObjectBox;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import io.objectbox.Box;

public class MeFragment extends Fragment {
    private FragmentMeBinding binding;
    private View root;
    private int numEgg = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMeBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        loadStatistics();
      
        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == 200) { // success
                        Intent data = result.getData();
                        Account account = (new Gson()).fromJson(data.getStringExtra("account"), Account.class);
                        binding.nameProfile.setText(account.getUsername());
                    }
                }
        );

        binding.profile.setOnClickListener($ -> {
            Intent intent = new Intent(root.getContext(), SigninActivity.class);
            activityResultLauncher.launch(intent);
        });

        binding.darkModeSwitch.setOnCheckedChangeListener((view, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        binding.appVersion.setOnClickListener($ -> {
            numEgg += 1;
            if (numEgg == 5) {
                numEgg = 0;
                RotateAnimation rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(5000);
                rotate.setInterpolator(new LinearInterpolator());
                binding.appVersion.startAnimation(rotate);
            }
        });

        // TODO: register and login
        LoginHandler loginHandler = new LoginHandler();
        RegisterHandler registerHandler = new RegisterHandler();
//        Backend.getInst().register("coln@lin.sldf", "cnkjllj", "olin", registerHandler);
//        Backend.getInst().login("cnkjllj", "olin", loginHandler);
        //Backend.getInst().register("coln@lin.sdf", "dfkkjkghk0j", "olin", registerHandler);
//        Backend.getInst().login("colin", "colin", loginHandler);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStatistics();
    }

    public void loadStatistics() {
        new Thread(() -> {
            // DB
            LocalDate today = LocalDate.now();
            Box<UserStatistics> statisticsBox = ObjectBox.get().boxFor(UserStatistics.class);
            List<UserStatistics> statisticsRes = statisticsBox.getAll();
            UserStatistics statistics;
            if (statisticsRes == null || statisticsRes.size() == 0) {
                statistics = new UserStatistics().setFirstDate(today.minusDays(6).toString());
                statisticsBox.put(statistics);
            }
            else {
                // adjust statistics according to the date
                statistics = statisticsRes.get(0);
                LocalDate oldFirstDate = LocalDate.parse(statistics.getFirstDate());
                int deltaDays = (int) oldFirstDate.until(today.minusDays(6), ChronoUnit.DAYS);
                if (deltaDays > 6) {
                    statistics = new UserStatistics().setFirstDate(today.minusDays(6).toString());
                    statisticsBox.put(statistics);
                }
                else if (deltaDays > 0) {
                    ArrayList<Integer> oldStatistics = (ArrayList<Integer>) statistics.getEntitiesViewed().clone();
                    statistics.setFirstDate(today.minusDays(6).toString())
                            .setEntitiesViewed(new ArrayList<Integer>(){{
                                // add (7 - deltaDays) items
                                for (int oldIndex = deltaDays; oldIndex < 7; oldIndex++)
                                    add(oldStatistics.get(oldIndex));
                                for (int i = deltaDays; i < 7; i++)
                                    add(0);
                            }}
                    );
                    statisticsBox.put(statistics);
                }
            }
            List<Integer> value = statistics.getEntitiesViewed();
            List<String> key = new ArrayList<String>(){{
                for (int i = 7; i > 0; i--) {
                    LocalDate thisDay = today.minusDays(i);
                    add(thisDay.getMonthValue() + "/" + thisDay.getDayOfMonth());
                }
            }};
            List<Entry> entries = new ArrayList<Entry>() {{
                for (int i = 0; i < key.size(); ++i)
                    add(new Entry(i, value.get(i)));
            }};
            getActivity().runOnUiThread(() -> {
                LineDataSet dataset = new LineDataSet(entries, "近一周浏览量"); // add entries to dataset
                TypedValue typedValue = new TypedValue();
                root.getContext().getTheme().resolveAttribute(R.attr.colorOnPrimary, typedValue, true);
                int color = ContextCompat.getColor(root.getContext(), typedValue.resourceId);
                dataset.setValueTextSize(10);
                dataset.setValueTextColor(color);
                dataset.setDrawFilled(true);
                dataset.setFillDrawable(ContextCompat.getDrawable(root.getContext(), R.drawable.gradient_fill));
                LineChart lineChart = binding.lineChart;
                lineChart.setClipValuesToContent(false);
                lineChart.getXAxis().setTextColor(color);
                typedValue = new TypedValue();
                root.getContext().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
                color = ContextCompat.getColor(root.getContext(), typedValue.resourceId);
                dataset.setColor(color);

                LineData lineData = new LineData(dataset);
                lineChart.setDragEnabled(false);
                lineChart.setScaleEnabled(false);
                lineChart.setScaleXEnabled(false);
                lineChart.setScaleYEnabled(false);
                lineChart.setPinchZoom(false);
                lineChart.getDescription().setEnabled(false);
                lineChart.getLegend().setEnabled(false);
                lineChart.getAxisRight().setEnabled(false);
                lineChart.getAxisLeft().setEnabled(false);
                lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(key));
                lineChart.getXAxis().setDrawGridLines(false);
                lineChart.getAxisLeft().setDrawGridLines(false);
                lineChart.getAxisLeft().setStartAtZero(true);
                lineChart.setData(lineData);
                lineChart.invalidate();
            });
        }).start();
    }

    static protected void initUser() {
        Box<UserStatistics> statisticsBox = ObjectBox.get().boxFor(UserStatistics.class);
        statisticsBox.removeAll();
        UserStatistics statistics = new UserStatistics()
                .setFirstDate(LocalDate.now().toString())
                .setEntitiesViewed(new ArrayList<>(
                        Collections.nCopies(7, 0)
                ));
        statisticsBox.put(statistics);
        Backend.getInst().uploadUserStatistics(statistics, new InitUserHandler());
    }

    static class RegisterHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.i("MeFragment/RegisterHandler", String.valueOf(msg.what));
            if (msg.what == 0 && msg.obj != null) {
                Account account = (Account) msg.obj;
                initUser();

                // TODO
            }
            else {  // register failed
                if (((AtomicReference<String>) msg.obj).toString().contains("duplicated")) {
                    // TODO
                }
            }
        }
    }

    static class LoginHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.i("MeFragment/LoginHandler", String.valueOf(msg.what));
            if (msg.what == 0 && msg.obj != null) {
                Account account = (Account) msg.obj;
                // TODO
            }
            else {  // register failed
                if (((String) msg.obj).contains("login failed")) {
                    // TODO: incorrect username or password
                }
            }
        }
    }

    static class InitUserHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.i("MeFragment/InitUserHandler", String.valueOf(msg.what));
            if (msg.what == 0 && msg.obj != null) {
                //Account account = (Account) msg.obj;

                // TODO
            }
            else {  // register failed
                if (((String) msg.obj).contains("duplicated")) {
                    // TODO
                }
            }
        }
    }
}

//     static protected void initUser() {
//         Box<UserStatistics> statisticsBox = ObjectBox.get().boxFor(UserStatistics.class);
//         statisticsBox.removeAll();
//         UserStatistics statistics = new UserStatistics()
//                 .setFirstDate(LocalDate.now().toString())
//                 .setEntitiesViewed(new ArrayList<>(
//                         Collections.nCopies(7, 0)
//                 ));
//         statisticsBox.put(statistics);
//         Backend.getInst().uploadUserStatistics(statistics, new InitUserHandler());
//     }
