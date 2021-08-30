package com.tea.ilearn.activity.account;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.florent37.viewtooltip.ViewTooltip;
import com.tea.ilearn.databinding.ActivitySignupBinding;

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
            // TODO database
        });

//        TODO ?
//        binding.serviceTerm.setOnClickListener($->{});
//        binding.privacyPolicy.setOnClickListener($->{});
    }
}