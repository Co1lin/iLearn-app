package com.tea.ilearn.activity.account;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.tea.ilearn.databinding.ActivityForgetPasswordBinding;
import com.tea.ilearn.net.backend.Backend;

import java.util.concurrent.atomic.AtomicReference;

public class ForgetPasswordActivity extends AppCompatActivity {
    ActivityForgetPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        binding.username.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                String username = binding.username.getText().toString();
                Backend.getInst().checkUsername(username, new CheckUsernameHandler(binding.usernameBox));
            } else {
                binding.usernameBox.setError(null);
            }
        });

        binding.getVerifycode.setOnClickListener($ -> {
            // TODO ask backend to send verification code to email
        });

        binding.submit.setOnClickListener($ -> {
//            binding.email.getText().toString();
//            binding.verifycode.getText().toString();
            // TODO validate the verification code with backend
        });
    }


    static class CheckUsernameHandler extends Handler {
        TextInputLayout inputBox;

        public CheckUsernameHandler(TextInputLayout inputBox) {
            this.inputBox = inputBox;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1 && msg.obj != null && (((AtomicReference<String>)(msg.obj)).get().contains("already occupied"))) {
            } else {
                inputBox.setError("用户名不存在");
            }
        }
    }
}