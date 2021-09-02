package com.tea.ilearn.net.backend;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.tea.ilearn.net.APIRequest;

import java.util.HashMap;

/**
 * Network communication to EduKG
 */
public class Backend extends APIRequest {
    private Backend() {
        super(
                "https://api.ilearn.enjoycolin.top",
                "",
                "",
                new HashMap<String, Object>(){{
                    put("username", "colin");
                    put("password", "colin");
                }},
                "Authorization",
                true,
                "login failed, password incorrect",
                LoginResponse.class
        );
    }

    private static Backend instance = new Backend();
    public static Backend getInst() {
        return instance;
    }

    protected static int successCode = 0;

    @Override
    protected void onRefreshSuccess(Object response) {
        synchronized (tokenValue) {
            tokenValue = ((LoginResponse) response).getToken();
        }
    }

    protected void login(String username, String password, Handler handler) {
        loginParams = new HashMap<String, Object>(){{
            put("username", username);
            put("password", password);
        }};
        asyncRefresh(handler);
    }

    class RegisterCallbackHandler extends Handler {
        private Handler originalHandler;

        public RegisterCallbackHandler(Handler _originalHandler) {
            originalHandler = _originalHandler;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1)
                Message.obtain(originalHandler, 1, "register failed!").sendToTarget();
            else {
                tokenValue = ((LoginResponse) msg.obj).getToken();
                Message.obtain(originalHandler, 0, msg.obj).sendToTarget();
            }
        }
    }

    protected void register(String email, String username,
                            String password, Handler handler) {
        POSTJson("/register",
                new HashMap<String, Object>(){{
                    put("email", email);
                    put("username", username);
                    put("password", password);
                }},
                p -> p.asResponse(LoginResponse.class),
                new RegisterCallbackHandler(handler)
        );
    }

    protected void changePassword(String oldPassword, String newPassword,
                                  String username, Handler handler) {
        POSTJson("/users/changepassword",
                new HashMap<String, Object>(){{
                    put("username", username);
                    put("new_password", newPassword);
                    put("old_password", oldPassword);
                }},
                p -> p.asResponse(ChangePasswdResponse.class),
                handler
        );
        // TODO callback?
    }

    protected void checkUsername(String username, Handler handler) {

    }

    protected void checkPassword(String password, Handler handler) {

    }
    // TODO reset password by email? ask TA

}
