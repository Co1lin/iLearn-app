package com.tea.ilearn.net.backend;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    int code;
    String message;
    public class Data {
        @SerializedName("token")
        String token;
    }
    Data data;

    public String getToken() {
        return data.token;
    }
}
