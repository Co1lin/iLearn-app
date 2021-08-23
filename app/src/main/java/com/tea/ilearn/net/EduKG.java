package com.tea.ilearn.net;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import rxhttp.wrapper.param.RxHttp;

/**
 * Network communication to EduKG
 */
public class EduKG {
    private static String authId = "";
    private static int maxRetries = 5;
    private static int maxLoginRetries = 3;

    public static int successCode = 0;
    public static int failureCode = -1;
    public static int timeoutSeconds = 5;
    public static String loginFailedMessage = "请先登录";

    public static void refresh() {
        Log.i("EduKG.refresh", "refreshing id");
        RxHttp.postForm("")
                .setDomainToEduKGLoginIfAbsent()
                .setSync()
                .add("phone", "13717683859")
                .add("password", "abc123456")
                .asClass(LoginResponse.class)
                .subscribe(response -> {
                    authId = response.id;
                    Log.i("EduKG.refresh", "id updated: " + authId);
                }, throwable -> {
                    Log.e("EduKG.refresh", "login error: " + throwable.toString());
                });
    }

    /**
     * Perform GET Request to EduKG Open API
     * @param responseClass Class to bind with the "data" field in raw JSON response
     * @param path          Relative path to Url.eduKGOpenUrl
     * @param params        Map containing key-value pairs to add to GET params
     * @param handler       Handler to be sent the object of responseClass
     */
    public static void getRequest(Class<?> responseClass,
                                  String path,
                                  Map<String, ?> params,
                                  Handler handler) {
        new Thread(() -> {
            AtomicBoolean loginFailed = new AtomicBoolean(false);
            AtomicBoolean handlerSent = new AtomicBoolean(false);
            int loopCounter = 0;
            do {
                loopCounter++;
                loginFailed.set(false);
                RxHttp.get(path)
                        .setDomainToEduKGOpenIfAbsent()
                        .setSync()
                        .addAll(params)
                        .asResponseList(responseClass)
                        .timeout(timeoutSeconds, TimeUnit.SECONDS)
                        .retry(maxRetries, throwable -> {
                            Log.i("EduKG.getRequest", "retry: " + throwable.getMessage());
                            if (throwable.getMessage().equals(loginFailedMessage)) {
                                refresh();
                                loginFailed.set(true);
                                return false;
                            }
                            return true;
                        })
                        .subscribe(respObj -> {
                            Message.obtain(handler, 0, respObj).sendToTarget();
                            handlerSent.set(true);
                        }, throwable -> {
                            Log.e("EduKG.getRequest", "error: " + throwable.getMessage());
                        });
            } while (loginFailed.get() && loopCounter < maxLoginRetries);
            if (!handlerSent.get())
                Message.obtain(handler, 1).sendToTarget();  // send failure message
        }).start();
    }

    public static void entitySearch(String course, String searchKey, Handler handler) {
        // TODO: cancel when return
        getRequest(Entity.class,
                "instanceList",
                new HashMap<String, Object>(){{
                    put("id", authId);
                    put("course", course);
                    put("searchKey", searchKey);
                }},
                handler);
    }

}