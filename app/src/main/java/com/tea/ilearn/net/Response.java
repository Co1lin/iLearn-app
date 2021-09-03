package com.tea.ilearn.net;

import com.google.gson.annotations.SerializedName;

public class Response<T> {
    private int     code;
    @SerializedName(value = "msg", alternate = "message")
    private String  msg;
    private T       data;

    public T getData() {
        return data;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
