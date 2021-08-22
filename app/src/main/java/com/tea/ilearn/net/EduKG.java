package com.tea.ilearn.net;

import android.util.Log;

import rxhttp.wrapper.param.RxHttp;

public class EduKG {
    private static final EduKG instance = new EduKG();
    private EduKG() {
        refresh();
    }
    public static EduKG getInstance() {
        return instance;
    }

    private String authId = "";

    public void refresh() {
        RxHttp.postForm("")
                .setDomainToEduKGLoginIfAbsent()
                .add("phone", "13717683859")
                .add("password", "abc123456")
                .asClass(LoginResponse.class)
                .subscribe(response -> {
                    authId = response.id;
                }, throwable -> {
                    Log.e("EduKG login error", throwable.toString());
                });
    }


}