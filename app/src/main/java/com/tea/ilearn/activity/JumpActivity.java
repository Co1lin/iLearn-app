package com.tea.ilearn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.tea.ilearn.MainActivity;

public class JumpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ilearn://share/
        Intent intent = getIntent();
        String path = intent.getDataString();
        String[] splitedPath = path.split("ilearn://share/");
        path = splitedPath[1];
        Log.e("JumpActivity", path);
        if (path.contains("exercise")) {

        }
        else if (path.contains("main")) {
            Intent anotherIntent = new Intent(this, MainActivity.class);
            startActivity(anotherIntent);
        }
    }
}