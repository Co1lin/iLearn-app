package com.tea.ilearn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.tea.ilearn.MainActivity;
import com.tea.ilearn.activity.entity_detail.EntityDetailActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class JumpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // match ilearn://share/ or http://api.ilearn.enjoycolin.top/app/
        String[] pattern = { "ilearn://share/", "http://api.ilearn.enjoycolin.top/app/" };
        Intent intent = getIntent();
        String uri = intent.getDataString();
        Log.i("JumpActivity", uri);
        if (uri.contains(pattern[0]))
            uri = uri.split(pattern[0])[1];
        else if (uri.contains(pattern[1]))
            uri = uri.split(pattern[1])[1];
        else {  // unexpected, just open it
            Intent anotherIntent = new Intent(this, MainActivity.class);
            startActivity(anotherIntent);
            return;
        }

        if (uri.startsWith("entity/")){
            uri = uri.split("entity/")[1];
            Intent anotherIntent = new Intent(this, EntityDetailActivity.class);
            anotherIntent.setAction(Intent.ACTION_SEARCH);
            try {
                JumpEntity info = (new Gson()).fromJson(URLDecoder.decode(uri, StandardCharsets.UTF_8.name()), JumpEntity.class);
                anotherIntent.putExtra("name", info.name);
                anotherIntent.putExtra("category", info.category);
                anotherIntent.putExtra("subject", info.subject);
                anotherIntent.putExtra("id", info.id);
                anotherIntent.putStringArrayListExtra("categories", info.categories);
                startActivity(anotherIntent);
                finish();
            } catch (UnsupportedEncodingException e) {
                Log.e("JumpActivity", e.toString());
            }
        }
    }

    public static class JumpEntity {
        String name, subject, category, id;
        ArrayList<String> categories;

        public JumpEntity(String name, String subject, String category, String id, ArrayList<String> categories) {
            this.name = name;
            this.subject = subject;
            this.category = category;
            this.id = id;
            this.categories = categories;
        }
    }
}