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

import com.github.florent37.viewtooltip.ViewTooltip;
import com.google.gson.Gson;
import com.tea.ilearn.R;
import com.google.android.material.textfield.TextInputLayout;
import com.tea.ilearn.databinding.ActivitySignupBinding;
import com.tea.ilearn.model.Account;
import com.tea.ilearn.net.backend.Backend;

import java.util.concurrent.atomic.AtomicReference;

public class SignupActivity extends AppCompatActivity {
    private ActivitySignupBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        binding.tosignin.setOnClickListener($ -> {
            finish(); // TODO clear previous signin activity info ?
        });

        binding.signup.setOnClickListener($ -> {
            if (!binding.agree.isChecked()) {
                ViewTooltip.on(binding.agree)
                        .autoHide(true, 1000)
                        .corner(30)
                        .position(ViewTooltip.Position.TOP)
                        .text("请先勾选同意")
                        .show();
                return;
            }
            binding.progressCircular.setVisibility(View.VISIBLE);
            SignupHandler handler = new SignupHandler(binding, this);
            Backend.getInst().register(
                    binding.email.getText().toString(),
                    binding.username.getText().toString(),
                    binding.password.getText().toString(),
                    handler
            );
        });

        binding.username.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                String username = binding.username.getText().toString();
                Backend.getInst().checkUsername(username, new CheckUsernameHandler(binding.usernameBox));
            } else {
                binding.usernameBox.setError(null);
            }
        });

        binding.email.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {

            } else {
                binding.emailBox.setError(null);
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

        binding.progressCircular.setOnTouchListener((view, event) -> {
            return true;
        });
    }


    static class SignupHandler extends Handler {
        ActivitySignupBinding binding;
        AppCompatActivity that;

        SignupHandler(ActivitySignupBinding binding, AppCompatActivity that) {
            this.binding = binding;
            this.that = that;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            binding.progressCircular.setVisibility(View.GONE);
            if (msg.what == 1) {
                String err = ((AtomicReference<String>)msg.obj).get();
                if (msg.obj != null) {
                    if (err.contains("email")) {
                        binding.emailBox.setError("邮箱不合法");
                    }
                    if (err.contains("username")) {
                        if (err.contains("duplicated")) {
                        } else {
                            binding.usernameBox.setError("用户名长度应在3~20个字符之间");
                        }
                    }
                    if (err.contains("resolve")) {
                        Toast.makeText(binding.getRoot().getContext(), binding.getRoot().getContext().getResources().getString(R.string.network_retry), Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else if (msg.obj != null) {
                Account account = (Account) msg.obj;
                Intent intent = new Intent();
                intent.putExtra("account", (new Gson()).toJson(account));
                that.setResult(0, intent);
                that.finish();
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
            if (msg.what == 1 && msg.obj != null) {
                if (((AtomicReference<String>)msg.obj).get().contains("already occupied")) {
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