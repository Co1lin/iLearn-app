package com.tea.ilearn.activity.account;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tea.ilearn.databinding.ActivityChangePasswordBinding;
import com.tea.ilearn.net.backend.Backend;

import java.util.concurrent.atomic.AtomicReference;

public class ChangePasswordActivity extends AppCompatActivity {
    private ActivityChangePasswordBinding binding;
    View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        root = binding.getRoot();
        setContentView(root);

        binding.newPassword.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                if (binding.newPassword.getText().toString().length() < 6) {
                    binding.newPasswordBox.setError("密码长度至少为6");
                }
            } else {
                binding.newPasswordBox.setError(null);
            }
        });

        binding.oldPassword.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) {
                binding.oldPasswordBox.setError(null);
            }
        });

        binding.modify.setOnClickListener($ -> {
            binding.progressCircular.setVisibility(View.VISIBLE);
            String oldPassword = binding.oldPassword.getText().toString();
            String newPassword = binding.newPassword.getText().toString();
            Backend.getInst().changePassword(oldPassword, newPassword,
                    new ChangePasswordHandler(binding, this));
        });


    }

    static class ChangePasswordHandler extends Handler {
        ActivityChangePasswordBinding binding;
        ChangePasswordActivity activity;

        public ChangePasswordHandler(ActivityChangePasswordBinding binding,
                                     ChangePasswordActivity activity) {
            this.binding = binding;
            this.activity = activity;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            binding.progressCircular.setVisibility(View.GONE);
            if (msg.what == 0) {
                Toast.makeText(binding.getRoot().getContext(),
                        "修改密码成功！", Toast.LENGTH_SHORT).show();
                activity.finish();
            }
            else if (msg.what == 1 && msg.obj != null) {
                String error = ((AtomicReference<String>)msg.obj).get();
                if (error.contains("old password incorrect"))
                    binding.oldPasswordBox.setError("当前密码错误");
                else // if (error.contains("runtime exception"))
                    Toast.makeText(binding.getRoot().getContext(),
                            "修改密码失败，请检查信息填写是否正确！", Toast.LENGTH_SHORT).show();
            }
        }
    }
}