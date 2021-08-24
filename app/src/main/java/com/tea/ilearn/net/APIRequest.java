package com.tea.ilearn.net;

import static java.lang.Thread.sleep;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import rxhttp.wrapper.param.RxHttp;
import rxhttp.wrapper.param.RxHttpFormParam;
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
        tokenValue = "123";
        loginFailedMessage = _loginFailedMessage;
        loginResponseClass = _loginResponseClass;
    }

    protected static int maxRetries = 2;
    protected static int maxLoginRetries = 2;
    protected static int timeoutSeconds = 8;
    protected static int retryIntervalSeconds = 3;

    protected abstract void onRefreshSuccess(Object response);

    public boolean syncRefresh() {
        Log.i("APIRequest.refresh", "refreshing");
        AtomicBoolean success = new AtomicBoolean(false);
        RxHttp.postForm(baseUrl + refreshPath)
                .setSync()
                .addAll(loginParams)
                .asClass(loginResponseClass)
                .timeout(3, TimeUnit.SECONDS)
                .subscribe(response -> {
                    onRefreshSuccess(response);
                    success.set(true);
                    Log.i("APIRequest.refresh", ": onRefreshSuccess completed");
                }, throwable -> {
                    Log.e("APIRequest.refresh", "login error: " + throwable.toString());
                });
        return success.get();
    }

    /**
     * Generic Request to API
     * @param _p                Object returned by RxHttp.<request method>(url)
     * @param params            Map containing key-value pairs to add to GET params
     * @param responseDefiner   Object of ResponseDefiner interface to
     *                          define the type of "data" field in Response<T>
     * @param handler           Handler to be sent the object of responseClass
     */
    public void Request(
            RxHttp _p,
            Map<String, Object> params,
            ResponseDefiner responseDefiner,
            Handler handler) {
        new Thread(() -> {
            AtomicBoolean loginFailed = new AtomicBoolean(false);
            AtomicBoolean messageSent = new AtomicBoolean(false);
            int loopCounter = 0;
            do {
                loopCounter++;
                loginFailed.set(false);
                // BUG: new a RxHTTP to avoid unfix bug of RxHttp
                RxHttp p = getSyncRxHttp(_p).removeAllQuery();
                params.put(tokenName, tokenValue);
                paramAddAll(p, params);
                Log.i("APIRequest.Request",
                        p.getParam().getMethod().toString() + p.getParam().getUrl());
                responseDefiner
                    .define(p)
                    .timeout(timeoutSeconds, TimeUnit.SECONDS)
                    .retry(maxRetries, throwable -> {
                        Log.i("APIRequest.Request", "retry: " + throwable.getMessage());
                        if (throwable.getMessage().equals(loginFailedMessage)) {
                            syncRefresh();
                            loginFailed.set(true);
                            return false;
                        }
                        sleep(retryIntervalSeconds);
                        return true;
                    })
                    .subscribe(respObj -> {
                        Message.obtain(handler, 0, respObj).sendToTarget();
                        messageSent.set(true);
                    }, throwable -> {
                        if (!throwable.getMessage().equals(loginFailedMessage))
                            Log.e("APIRequest.Request", "error: " + throwable.getMessage());
                    });
            } while (loginFailed.get() && loopCounter < maxLoginRetries);
            if (!messageSent.get())
                Message.obtain(handler, 1).sendToTarget();  // send failure message
        }).start();
    }

    /**
     * GET Request to API
     * @param path              Relative path to baseUrl
     * @param params            Map containing key-value pairs to add to GET params
     * @param responseDefiner   Object of ResponseDefiner interface to
     *                          define the type of "data" field in Response<T>
     * @param handler           Handler to be sent the object of responseClass
     */
    public void GET(
            String path,
            Map<String, Object> params,
            ResponseDefiner responseDefiner,
            Handler handler) {
        Request(RxHttp.get(baseUrl + genericPath + path),
                params,
                responseDefiner,
                handler);
    }

    /**
     * POST Request to API with x-www-from-urlencoded body
     * @param path              Relative path to baseUrl
     * @param params            Map containing key-value pairs to add to GET params
     * @param responseDefiner   Object of ResponseDefiner interface to
     *                          define the type of "data" field in Response<T>
     * @param handler           Handler to be sent the object of responseClass
     */
    public void POST(
            String path,
            Map<String, Object> params,
            ResponseDefiner responseDefiner,
            Handler handler) {
        Request(RxHttp.postForm(baseUrl + genericPath + path),
                params,
                responseDefiner,
                handler);
    }

    public boolean syncDetectOnline() {
        return syncRefresh();
    }

    public void asyncDetectOnline(Handler handler) {
        new Thread(() -> {
            boolean isOnline = syncDetectOnline();
            Message.obtain(handler, 0, isOnline);
        }).start();
    }

    private RxHttp getSyncRxHttp(RxHttp _p) {
        if (_p instanceof RxHttpNoBodyParam)
            return new RxHttpNoBodyParam( ((RxHttpNoBodyParam) _p).getParam() ).setSync();
        else if (_p instanceof RxHttpFormParam) {
            return new RxHttpFormParam( ((RxHttpFormParam) _p).getParam() ).setSync();
        }
        throw new IllegalArgumentException("_p is instance of " + _p.getClass() + " which is not supported");
    }

    private RxHttp paramAdd(RxHttp p, String key, String value) {
        if (p instanceof RxHttpNoBodyParam)
            return ((RxHttpNoBodyParam) p).add(key, value);
        else if (p instanceof RxHttpFormParam) {
            return ((RxHttpFormParam) p).add(key, value);
        }
        throw new IllegalArgumentException("p is instance of " + p.getClass() + " which is not supported");
    }

    private RxHttp paramAddAll(RxHttp p, Map<String, ?> params) {
        if (p instanceof RxHttpNoBodyParam)
            return ((RxHttpNoBodyParam) p).addAll(params);
        else if (p instanceof RxHttpFormParam) {
            return ((RxHttpFormParam) p).addAll(params);
        }
        throw new IllegalArgumentException("p is instance of " + p.getClass() + " which is not supported");
    }
}