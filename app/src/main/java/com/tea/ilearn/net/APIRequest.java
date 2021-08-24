package com.tea.ilearn.net;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import rxhttp.wrapper.param.RxHttp;
import rxhttp.wrapper.param.RxHttpNoBodyParam;

public abstract class APIRequest {
    // variables need to be override
    protected String baseUrl;
    protected String refreshPath;
    protected String genericPath;
    protected Map<String, ?> loginParams;
    protected String tokenName;
    protected String tokenValue;
    protected String loginFailedMessage;
    protected Class<?> loginResponseClass;

    public APIRequest(
            String _baseUrl,
            String _refreshPath,
            String _genericPath,
            Map<String, ?> _loginParams,
            String _tokenName,
            String _loginFailedMessage,
            Class<?> _loginResponseClass
    ) {
        baseUrl = _baseUrl;
        refreshPath = _refreshPath;
        genericPath = _genericPath;
        loginParams = _loginParams;
        tokenName = _tokenName;
        loginFailedMessage = _loginFailedMessage;
        loginResponseClass = _loginResponseClass;
    }

    protected static int maxRetries = 3;
    protected static int maxLoginRetries = 2;
    protected static int timeoutSeconds = 5;

    protected abstract void onRefreshSuccess(Object response);

    public void refresh() {
        Log.i("APIRequest.refresh", "refreshing");
        RxHttp.postForm(baseUrl + refreshPath)
                .setSync()
                .addAll(loginParams)
                .asClass(loginResponseClass)
                .subscribe(response -> {
                    onRefreshSuccess(response);
                    Log.i("APIRequest.refresh", ": onRefreshSuccess completed");
                }, throwable -> {
                    Log.e("APIRequest.refresh", "login error: " + throwable.toString());
                });
    }

    /**
     * Perform GET Request to API
     * @param responseDefiner   Object of ResponseDefiner interface to
     *                          define the type of "data" field in Response<T>
     * @param path              Relative path to baseUrl
     * @param params            Map containing key-value pairs to add to GET params
     * @param handler           Handler to be sent the object of responseClass
     */
    public void GET(ResponseDefiner responseDefiner,
                           String path,
                           Map<String, ?> params,
                           Handler handler) {
        new Thread(() -> {
            AtomicBoolean loginFailed = new AtomicBoolean(false);
            AtomicBoolean messageSent = new AtomicBoolean(false);
            int loopCounter = 0;
            Log.i("APIRequest.GET", baseUrl + genericPath + path);
            do {
                loopCounter++;
                loginFailed.set(false);
                RxHttpNoBodyParam p = RxHttp
                        .get(baseUrl + genericPath + path)
                        .setSync()
                        .add(tokenName, tokenValue)
                        .addAll(params);
                responseDefiner
                        .define(p)
                        .timeout(timeoutSeconds, TimeUnit.SECONDS)
                        .retry(maxRetries, throwable -> {
                            Log.i("APIRequest.GET", "retry: " + throwable.getMessage());
                            if (throwable.getMessage().equals(loginFailedMessage)) {
                                refresh();
                                loginFailed.set(true);
                                return false;
                            }
                            return true;
                        })
                        .subscribe(respObj -> {
                            Message.obtain(handler, 0, respObj).sendToTarget();
                            messageSent.set(true);
                        }, throwable -> {
                            if (!throwable.getMessage().equals(loginFailedMessage))
                                Log.e("APIRequest.GET", "error: " + throwable.getMessage());
                        });
            } while (loginFailed.get() && loopCounter < maxLoginRetries);
            if (!messageSent.get())
                Message.obtain(handler, 1).sendToTarget();  // send failure message
        }).start();
    }

}
