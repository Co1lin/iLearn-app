package com.tea.ilearn.net;

import static java.lang.Thread.sleep;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import rxhttp.wrapper.param.RxHttp;
import rxhttp.wrapper.param.RxHttpFormParam;
import rxhttp.wrapper.param.RxHttpJsonParam;
import rxhttp.wrapper.param.RxHttpNoBodyParam;

/**
 * Generic class to request to API <br>
 * Extend this class with <strong><i>Singleton Design Pattern</i></strong> <br>
 * to request to a specific API
 */
public abstract class APIRequest {
    // variables need to be override
    protected String baseUrl;
    protected String refreshPath;
    protected String genericPath;
    // login / refresh token
    protected Map<String, ?> loginParams;
    protected String tokenName;
    protected String tokenValue;
    protected RxHttp loginMethod;
    protected boolean authHeader;
    protected String loginFailedMessage;
    protected ResponseDefiner loginRequestDefiner;

    public APIRequest(
            String _baseUrl,
            String _refreshPath,
            String _genericPath,
            Map<String, ?> _loginParams,
            String _tokenName,
            RxHttp _loginMethod,
            boolean _authHeader,
            String _loginFailedMessage,
            ResponseDefiner _loginRequestDefiner
    ) {
        baseUrl = _baseUrl;
        refreshPath = _refreshPath;
        genericPath = _genericPath;
        loginParams = _loginParams;
        tokenName = _tokenName;
        tokenValue = "123";
        loginMethod = _loginMethod;
        authHeader = _authHeader;
        loginFailedMessage = _loginFailedMessage;
        loginRequestDefiner = _loginRequestDefiner;
        listen();
    }

    /**
     * Task to request with priority; Comparable wrapper of params of Request<br>
     * Greater priority number means higher priority
     */
    protected class Task implements Comparable<Task> {
        RxHttp p;
        Map<String, Object> params;
        ResponseDefiner responseDefiner;
        Handler handler;
        Integer priority;

        public Task(
                RxHttp _p,
                Map<String, Object> _params,
                ResponseDefiner _responseDefiner,
                Handler _handler,
                Integer _priority
        ) {
            p = _p;
            params = _params;
            responseDefiner = _responseDefiner;
            handler = _handler;
            priority = _priority;
        }

        public Task(
                RxHttp _p,
                Map<String, Object> _params,
                ResponseDefiner _responseDefiner,
                Handler _handler
        ) {
            this(_p, _params, _responseDefiner, _handler, 0);
        }

        @Override
        public int compareTo(Task task) {
            return -priority.compareTo(task.priority);
        }
    }

    PriorityBlockingQueue<Task> queue = new PriorityBlockingQueue<>();

    protected static final int maxRetries = 2;
    protected static final int timeoutSeconds = 60;
    protected static final int retryIntervalSeconds = 2;
    // login / refresh token
    protected static final int maxLoginRetries = 2;
    protected Long lastRefreshSuccess = 0L;
    protected static final long minRefreshIntervalMillis = 5000;
    protected static final int maxConcurrentTasks = 20;
    protected static final int tasksIntervalSeconds = 1;

    protected abstract void onRefreshSuccess(Object response);

    public boolean syncRefresh(Handler handler) {
        synchronized(lastRefreshSuccess) {
            if (System.currentTimeMillis() - lastRefreshSuccess < minRefreshIntervalMillis)
                return true;
            Log.i("APIRequest.refresh", "refreshing");
            AtomicBoolean success = new AtomicBoolean(false);
            RxHttp p = getSyncRxHttp(loginMethod);
            paramAddAll(p, loginParams);
            loginRequestDefiner.define(p)
            .timeout(3, TimeUnit.SECONDS)
            .subscribe(response -> {
                onRefreshSuccess(response);
                success.set(true);
                lastRefreshSuccess = System.currentTimeMillis();
                if (handler != null)
                    Message.obtain(handler, 0, response).sendToTarget();
                Log.i("APIRequest.refresh", ": onRefreshSuccess completed");
            }, throwable -> {
                if (handler != null)
                    Message.obtain(handler, 1, throwable.getMessage()).sendToTarget();
                Log.e("APIRequest.refresh", "login error: " + throwable.getMessage());
            });
            return success.get();
        }
    }

    public void asyncRefresh(Handler handler) {
        synchronized(lastRefreshSuccess) {
            new Thread(() -> {
                syncRefresh(handler);
            }).start();
        }
    }

    /**
     * Generic Request to API (Run in new thread)
     * @param _p                Object returned by RxHttp.<request method>(url)
     * @param params            Map containing key-value pairs to add to GET params
     * @param responseDefiner   Object of ResponseDefiner interface to
     *                          define the type of "data" field in Response<T>
     * @param handler           Handler to be sent the object of responseClass
     */
    protected void Request(
            RxHttp _p,
            Map<String, Object> params,
            ResponseDefiner responseDefiner,
            Handler handler
    ) {
        new Thread(() -> {
            AtomicBoolean loginFailed = new AtomicBoolean(false);
            AtomicBoolean messageSent = new AtomicBoolean(false);
            AtomicReference<String> lastErrorMessage = new AtomicReference<>();
            int loopCounter = 0;
            do {
                loopCounter++;
                loginFailed.set(false);
                // BUG: new a RxHTTP to avoid unfixed bug of RxHttp
                RxHttp p = getSyncRxHttp(_p);
                if (authHeader)
                    p.addHeader(tokenName, tokenValue);
                else
                    params.put(tokenName, tokenValue);
                paramAddAll(p, params);
                Log.i("APIRequest.Request",
                        p.getParam().getMethod().toString() + " " + p.getParam().getUrl());
                responseDefiner
                    .define(p)
                    .timeout(timeoutSeconds, TimeUnit.SECONDS)
                    .retry(maxRetries, throwable -> {
                        Log.i("APIRequest.Request", "retry: " + throwable.getMessage() + " " + p.getParam().getUrl());
                        if (throwable.getMessage().equals(loginFailedMessage)) {
                            syncRefresh(null);
                            loginFailed.set(true);
                            return false;
                        }
                        double interval = retryIntervalSeconds - 0.5 + Math.random();
                        sleep((long) (interval * 1000));
                        return true;
                    })
                    .subscribe(respObj -> {
                        Message.obtain(handler, 0, respObj).sendToTarget();
                        messageSent.set(true);
                    }, throwable -> {
                        if (!throwable.getMessage().equals(loginFailedMessage)) {
                            Log.e("APIRequest.Request", "error: " + throwable.getMessage() + " " + p.getParam().getUrl());
                            lastErrorMessage.set(throwable.getMessage());
                        }
                    });
            } while (loginFailed.get() && loopCounter < maxLoginRetries);
            if (!messageSent.get())
                Message.obtain(handler, 1, lastErrorMessage).sendToTarget();  // send failure message
        }).start();
    }

    /**
     * Thread running in background to start request tasks retrieved from queue <br>
     * Limit the concurrency by average simulation
     */
    private void listen() {
        new Thread(() -> { while (true) {
            try {
                Task task = queue.take();
                Request(task.p, task.params, task.responseDefiner, task.handler);
                sleep(tasksIntervalSeconds * 1000 / maxConcurrentTasks);
            } catch (InterruptedException e) {
                Log.e("APIRequest.listen", e.toString());
            }
        }}).start();
    }

    private void addTask(Task task) {
        queue.put(task);
    }

    /**
     * GET Request to API
     * @param path              Relative path to baseUrl
     * @param params            Map containing key-value pairs to add to GET params
     * @param responseDefiner   Object of ResponseDefiner interface to
     *                          define the type of "data" field in Response<T>
     * @param handler           Handler to be sent the object of responseClass
     * @param priority          Priority of this request in the blocking queue
     */
    public void GET(
            String path,
            Map<String, Object> params,
            ResponseDefiner responseDefiner,
            Handler handler,
            int priority
    ) {
        addTask(new Task(
                RxHttp.get(baseUrl + genericPath + path),
                params,
                responseDefiner,
                handler,
                priority
        ));
    }

    public void GET(
            String path,
            Map<String, Object> params,
            ResponseDefiner responseDefiner,
            Handler handler
    ) {
        GET(path, params, responseDefiner, handler, 0);
    }

    /**
     * POST Request to API with x-www-from-urlencoded body
     * @param path              Relative path to baseUrl
     * @param params            Map containing key-value pairs to add to GET params
     * @param responseDefiner   Object of ResponseDefiner interface to
     *                          define the type of "data" field in Response<T>
     * @param handler           Handler to be sent the object of responseClass
     * @param priority          Priority of this request in the blocking queue
     */
    public void POSTForm(
            String path,
            Map<String, Object> params,
            ResponseDefiner responseDefiner,
            Handler handler,
            int priority
    ) {
        addTask(new Task(
                RxHttp.postForm(baseUrl + genericPath + path),
                params,
                responseDefiner,
                handler,
                priority
        ));
    }

    public void POSTForm(
            String path,
            Map<String, Object> params,
            ResponseDefiner responseDefiner,
            Handler handler
    ) {
        POSTForm(path, params, responseDefiner, handler, 0);
    }

    public void POSTJson(
            String path,
            Map<String, Object> params,
            ResponseDefiner responseDefiner,
            Handler handler,
            int priority
    ) {
        addTask(new Task(
                RxHttp.postJson(baseUrl + genericPath + path),
                params,
                responseDefiner,
                handler,
                priority
        ));
    }

    public void POSTJson(
            String path,
            Map<String, Object> params,
            ResponseDefiner responseDefiner,
            Handler handler
    ) {
        POSTJson(path, params, responseDefiner, handler, 0);
    }

    public boolean syncDetectOnline() {
        return syncRefresh(null);
    }

    public void asyncDetectOnline(Handler handler) {
        new Thread(() -> {
            boolean isOnline = syncDetectOnline();
            Message.obtain(handler, 0, isOnline);
        }).start();
    }

    private RxHttp getSyncRxHttp(RxHttp _p) {
        if (_p instanceof RxHttpNoBodyParam)
            return new RxHttpNoBodyParam( ((RxHttpNoBodyParam) _p).getParam() ).setSync().removeAllQuery();
        else if (_p instanceof RxHttpFormParam) {
            return new RxHttpFormParam( ((RxHttpFormParam) _p).getParam() ).setSync().removeAllBody();
        }
        else if (_p instanceof RxHttpJsonParam) {
            return new RxHttpJsonParam( ((RxHttpJsonParam) _p).getParam() ).setSync().removeAllQuery();
        }
        throw new IllegalArgumentException("_p is instance of " + _p.getClass() + " which is not supported");
    }

    private RxHttp paramAdd(RxHttp p, String key, String value) {
        if (p instanceof RxHttpNoBodyParam)
            return ((RxHttpNoBodyParam) p).add(key, value);
        else if (p instanceof RxHttpFormParam) {
            return ((RxHttpFormParam) p).add(key, value);
        }
        else if (p instanceof RxHttpJsonParam) {
            return ((RxHttpJsonParam) p).add(key, value);
        }
        throw new IllegalArgumentException("p is instance of " + p.getClass() + " which is not supported");
    }

    private RxHttp paramAddAll(RxHttp p, Map<String, ?> params) {
        if (p instanceof RxHttpNoBodyParam)
            return ((RxHttpNoBodyParam) p).addAll(params);
        else if (p instanceof RxHttpFormParam) {
            return ((RxHttpFormParam) p).addAll(params);
        }
        else if (p instanceof RxHttpJsonParam) {
            return ((RxHttpJsonParam) p).addAll(params);
        }
        throw new IllegalArgumentException("p is instance of " + p.getClass() + " which is not supported");
    }
}