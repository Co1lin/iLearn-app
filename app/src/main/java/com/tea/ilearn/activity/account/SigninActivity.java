package com.tea.ilearn.activity.account;

import android.content.Context;
import android.content.Intent;
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

import com.google.gson.Gson;
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

        binding.username.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                String username = binding.username.getText().toString();
                if (username.length() == 0) {
                    binding.usernameBox.setError("请输入用户名");
                } else {
//                    Backend.getInst().checkUsername(username, new CheckUsernameHandler(binding.usernameBox));
                }
            } else {
                binding.usernameBox.setError(null);
            }
        });

        binding.password.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                if (binding.password.getText().toString().length() == 0) {
                    binding.passwordBox.setError("请输入密码");
                }
            } else {
                binding.passwordBox.setError(null);
            }
        });

        binding.signin.setOnClickListener($ -> {
            if (binding.usernameBox.getError() != null || binding.passwordBox.getError() != null) {
                Toast.makeText(this, "请检查输入", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.progressCircular.setVisibility(View.VISIBLE);
            SigninHandler signinHandler = new SigninHandler(binding, this);
            Backend.getInst().login(
                    binding.username.getText().toString(),
                    binding.password.getText().toString(),
                    signinHandler
            );
        });

        binding.tosignup.setOnClickListener($ -> {
            Intent intent = new Intent(root.getContext(), SignupActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            root.getContext().startActivity(intent);
            finish();
        });

        binding.toforget.setOnClickListener($ -> {
            Intent intent = new Intent(root.getContext(), ForgetPasswordActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            root.getContext().startActivity(intent);
            finish();
        });
    }

    static class SigninHandler extends Handler {
        ActivitySigninBinding binding;
        AppCompatActivity that;

        SigninHandler(ActivitySigninBinding binding, AppCompatActivity that) {
            this.binding = binding;
            this.that = that;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            binding.progressCircular.setVisibility(View.GONE);
            if (msg.what == 0 && msg.obj != null) {
                Account account = (Account) msg.obj;
                Intent intent = new Intent();
                intent.putExtra("account", (new Gson()).toJson(account));
                that.setResult(200, intent);
                that.finish();
            }
            else { // signin failed
                String err = (String)(msg.obj);
                if (err.contains("login failed")) {
                    Toast.makeText(that, "用户名或密码错误", Toast.LENGTH_SHORT).show();
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

//    static class CheckUsernameHandler extends Handler {
//        TextInputLayout inputBox;
//
//        public CheckUsernameHandler(TextInputLayout inputBox) {
//            this.inputBox = inputBox;
//        }
//
//        @Override
//        public void handleMessage(@NonNull Message msg) {
//            super.handleMessage(msg);
//            if (msg.what == 1 && msg.obj != null && ((AtomicReference<String>)msg.obj).get().contains("already occupied")) {
//                // user name exist
//            } else{
//                inputBox.setError("用户名不存在");
//            }
//        }
//    }
}