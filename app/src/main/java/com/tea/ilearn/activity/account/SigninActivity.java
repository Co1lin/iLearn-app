package com.tea.ilearn.activity.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

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