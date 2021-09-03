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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.florent37.viewtooltip.ViewTooltip;
import com.tea.ilearn.R;
import com.google.android.material.textfield.TextInputLayout;
import com.tea.ilearn.databinding.ActivitySignupBinding;
import com.tea.ilearn.model.Account;
import com.tea.ilearn.net.backend.Backend;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        // TODO validator
        binding.tosignin.setOnClickListener($ -> {
            finish(); // TODO clear previous signin activity info ?
            // TODO cannot register if logged in
        });
        binding.signup.setOnClickListener($ -> {
            if (!binding.agree.isChecked()) {
                ViewTooltip.on(binding.agree)
                    .autoHide(true, 1000)
                    .corner(30)
                    .position(ViewTooltip.Position.TOP)
                    .text("请先勾选同意")
                    .show();
            }
            RegisterHandler handler = new RegisterHandler();
            Backend.getInst().register(
                    binding.email.getText().toString(), binding.username.getText().toString(),
                    binding.password.getText().toString(), handler
            );
        });

        binding.username.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                String username = binding.username.getText().toString();
                Backend.getInst().checkUsername(username, new CheckUsernameHandler(binding.usernameBox));
            }
        });

        binding.servicePolicy.setOnClickListener($->{
            binding.policyDetail.setVisibility(View.VISIBLE);
            binding.policyText.setText(getResources().getString(R.string.service_policy));
        });
        binding.closePolicyDetail.setOnClickListener($->{
            binding.policyDetail.setVisibility(View.GONE);
        });
        binding.privacyPolicy.setOnClickListener($->{
            binding.policyDetail.setVisibility(View.VISIBLE);
            binding.policyText.setText(getResources().getString(R.string.privacy_policy));
        });
        binding.closePolicyDetail.setOnClickListener($->{
            binding.policyDetail.setVisibility(View.GONE);
        });
    }


    static class RegisterHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                // TODO failed, display error message
            }
            else if (msg.obj != null) {
                Account account = (Account) msg.obj;
                // TODO: display username
            }
        }
    }

    static class CheckUsernameHandler extends Handler {
        TextInputLayout inputBox;

        public CheckUsernameHandler(TextInputLayout inputBox) {
            this.inputBox = inputBox;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0 && msg.obj != null) {
                if (((String) msg.obj).contains("has been occupied")) {
                    inputBox.setError("该用户名已被占用");
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