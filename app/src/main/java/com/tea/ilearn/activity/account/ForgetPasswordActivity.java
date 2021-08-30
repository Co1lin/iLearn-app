package com.tea.ilearn.activity.account;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.tea.ilearn.databinding.ActivityForgetPasswordBinding;

public class ForgetPasswordActivity extends AppCompatActivity {
    ActivityForgetPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        binding.getVerifycode.setOnClickListener($ -> {
            // TODO ask backend to send verification code to email
        });
        binding.find.setOnClickListener($ -> {
//            binding.email.getText().toString();
//            binding.verifycode.getText().toString();
            // TODO validate the verification code with backend
        });
    }
}