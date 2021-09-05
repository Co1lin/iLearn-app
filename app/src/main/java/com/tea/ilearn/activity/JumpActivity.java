package com.tea.ilearn.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.tea.ilearn.MainActivity;
import com.tea.ilearn.activity.entity_detail.EntityDetailActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class JumpActivity extends AppCompatActivity {
    public static class JumpEntity {
        String name, subject, category, id;
        List<String> categories;

        public JumpEntity(String name, String subject, String category, String id, List<String> categories) {
            this.name = name;
            this.subject = subject;
            this.category = category;
            this.id = id;
            this.categories = categories;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ilearn://share/
        Intent intent = getIntent();
        String path = intent.getDataString();
        String[] splitedPath = path.split("ilearn://share/");
        path = splitedPath[1];
        splitedPath = path.split("/");
        String type = splitedPath[0];
        if (type.equals("exercise")) {

        }
        else if (type.equals("entity")) {
            Intent anotherIntent = new Intent(this, EntityDetailActivity.class);
            anotherIntent.setAction(Intent.ACTION_SEARCH);
            try {
                JumpEntity info = (new Gson()).fromJson(URLDecoder.decode(splitedPath[1], StandardCharsets.UTF_8.name()), JumpEntity.class);
                anotherIntent.putExtra("name", info.name);
                anotherIntent.putExtra("category", info.category);
                anotherIntent.putExtra("subject", info.subject);
                anotherIntent.putExtra("id", info.id);
                startActivity(anotherIntent);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else {
            Intent anotherIntent = new Intent(this, MainActivity.class);
            startActivity(anotherIntent);
        }
    }
}