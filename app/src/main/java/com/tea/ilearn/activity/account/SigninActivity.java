package com.tea.ilearn.activity.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.tea.ilearn.databinding.ActivitySigninBinding;

public class SigninActivity extends AppCompatActivity {
    private ActivitySigninBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        // TODO validation error base on database (below are error hint examples)
        /*
        binding.usernameBox.setError("用户名不存在"); // or 邮箱未注册
        binding.passwordBox.setError("密码错误");
        binding.usernameBox.setError(null);
        binding.passwordBox.setError(null);
         */

        binding.signin.setOnClickListener($ -> {
            // TODO signin account
            finish(); // TODO change previous activity's profile image and profile username
        });

        binding.tosignup.setOnClickListener($ -> {
            Intent intent = new Intent(root.getContext(), SignupActivity.class);
            root.getContext().startActivity(intent);
        });

        binding.toforget.setOnClickListener($ -> {
            Intent intent = new Intent(root.getContext(), ForgetPasswordActivity.class);
            root.getContext().startActivity(intent);
        });
    }
}