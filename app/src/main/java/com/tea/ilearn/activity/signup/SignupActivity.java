package com.tea.ilearn.activity.signup;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.tea.ilearn.databinding.ActivitySignupBinding;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

    }
}