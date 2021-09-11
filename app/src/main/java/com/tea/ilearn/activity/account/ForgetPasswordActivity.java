package com.tea.ilearn.activity.account;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.tea.ilearn.databinding.ActivityForgetPasswordBinding;
import com.tea.ilearn.net.backend.Backend;

import java.util.concurrent.atomic.AtomicReference;

public class ForgetPasswordActivity extends AppCompatActivity {
    ActivityForgetPasswordBinding binding;
    View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgetPasswordBinding.inflate(getLayoutInflater());
        root = binding.getRoot();
        setContentView(root);

        binding.username.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                String username = binding.username.getText().toString();
                Backend.getInst().checkUsername(username, new CheckUsernameHandler(binding.usernameBox));
            } else {
                binding.usernameBox.setError(null);
            }
        });

        binding.verifycode.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                binding.verifycodeBox.setError(null);
            }
        });

        binding.getVerifycode.setOnClickListener($ -> {
            // ask backend to send verification code to email
            binding.progressCircular.setVisibility(View.VISIBLE);
            Backend.getInst().forgetPassword(binding.username.getText().toString(), new GetCodeHandler(binding));
        });

        binding.newPassword.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                if (binding.newPassword.getText().toString().length() < 6) {
                    binding.newPasswordBox.setError("密码长度至少为6");
                }
            } else {
                binding.newPasswordBox.setError(null);
            }
        });

        binding.submit.setOnClickListener($ -> {
            // validate the verification code with backend
            binding.progressCircular.setVisibility(View.VISIBLE);
            String username = binding.username.getText().toString();
            String code = binding.verifycode.getText().toString();
            String newPassword = binding.newPassword.getText().toString();
            Backend.getInst().resetPassword(username, code, newPassword,
                    new ResetHandler(binding, this));
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
            if (msg.what == 1 && msg.obj != null
                && (((AtomicReference<String>)(msg.obj)).get().contains("already occupied"))) {
            }
            else {
                inputBox.setError("用户名不存在");
            }
        }
    }

    static class GetCodeHandler extends Handler {
        ActivityForgetPasswordBinding binding;

        public GetCodeHandler(ActivityForgetPasswordBinding binding) {
            this.binding = binding;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            binding.progressCircular.setVisibility(View.GONE);
            if (msg.what == 1 && msg.obj != null) {
                String error = ((AtomicReference<String>)msg.obj).get();
                if (error.contains("username not found"))
                    binding.usernameBox.setError("用户名不存在");
                else if (error.contains("mail address not found"))
                    binding.usernameBox.setError("该用户的邮箱地址无效");
                else
                    Toast.makeText(binding.getRoot().getContext(), "发生未知错误", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(binding.getRoot().getContext(), "已发送验证码，请查收！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    static class ResetHandler extends Handler {
        ActivityForgetPasswordBinding binding;
        ForgetPasswordActivity activity;

        public ResetHandler(ActivityForgetPasswordBinding binding,
                            ForgetPasswordActivity activity) {
            this.binding = binding;
            this.activity = activity;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            binding.progressCircular.setVisibility(View.GONE);
            if (msg.what == 0) {
                Toast.makeText(binding.getRoot().getContext(),
                        "重置密码成功！", Toast.LENGTH_SHORT).show();
                activity.finish();
            }
            else if (msg.what == 1 && msg.obj != null) {
                String error = ((AtomicReference<String>)msg.obj).get();
                if (error.contains("code incorrect"))
                    binding.verifycodeBox.setError("验证码错误");
                else // if (error.contains("runtime exception"))
                    Toast.makeText(binding.getRoot().getContext(),
                            "重置密码失败，请检查信息填写是否正确！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}