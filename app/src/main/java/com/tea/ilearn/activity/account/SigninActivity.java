package com.tea.ilearn.activity.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.tea.ilearn.databinding.ActivitySigninBinding;
import com.tea.ilearn.model.Account;
import com.tea.ilearn.net.backend.Backend;

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
            // TODO empty check ?
            SigninHandler signinHandler = new SigninHandler();
            Backend.getInst().login(
                    binding.username.getText().toString(),
                    binding.password.getText().toString(),
                    signinHandler
            );
            finish(); // TODO change previous activity's profile image and profile username
        });

        binding.tosignup.setOnClickListener($ -> {
            Intent intent = new Intent(root.getContext(), SignupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            root.getContext().startActivity(intent);
            finish();
        });

        binding.toforget.setOnClickListener($ -> {
            Intent intent = new Intent(root.getContext(), ForgetPasswordActivity.class);
            root.getContext().startActivity(intent);
        });
    }

    static class SigninHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.i("MeFragment/registerHandler", String.valueOf(msg.what));
            if (msg.what == 0 && msg.obj != null) {
                Account account = (Account) msg.obj;
                // TODO
            }
            else {  // register failed
                if (((String) msg.obj).contains("login failed")) {
                    // TODO: incorrect username or password
                }
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