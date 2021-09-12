package com.tea.ilearn.activity.account;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tea.ilearn.databinding.ActivityChangePasswordBinding;
import com.tea.ilearn.net.backend.Backend;
import com.tea.ilearn.utils.Checker;

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
                Checker.checkPassword(binding.newPassword, binding.newPasswordBox);
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
            if (Checker.checkPassword(binding.newPassword, binding.newPasswordBox) &&
                binding.oldPasswordBox.getError() == null) {
                binding.progressCircular.setVisibility(View.VISIBLE);
                String oldPassword = binding.oldPassword.getText().toString();
                String newPassword = binding.newPassword.getText().toString();
                Backend.getInst().changePassword(oldPassword, newPassword,
                        new ChangePasswordHandler(binding, this));
            }
            else
                return;
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

    /**
     * Ref <a href="https://stackoverflow.com/a/28939113">EditText, clear focus on touch outside</a>
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}