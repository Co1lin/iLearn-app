package com.tea.ilearn.utils;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Checker {
    public static boolean checkPassword(TextInputEditText passwordEditText, TextInputLayout passwordBox) {
        int len = passwordEditText.getText().toString().length();
        if (len < 6 || len > 32) {
            passwordBox.setError("密码长度应在 6 ~ 32 之间");
            return false;
        }
        return true;
    }
}
