package com.tea.ilearn.net.backend;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.tea.ilearn.model.Account;
import com.tea.ilearn.net.APIRequest;
import com.tea.ilearn.utils.ObjectBox;

import java.util.HashMap;
import java.util.List;

import io.objectbox.Box;
import rxhttp.wrapper.param.RxHttp;

/**
 * Network communication to EduKG
 */
public class Backend extends APIRequest {

    Box<Account> accountBox;

    private Backend() {
        super(
                "https://api.ilearn.enjoycolin.top",
                "/login",
                "",
                new HashMap<String, Object>(){{
                    put("username", "");
                    put("password", "");
                }},
                "Authorization",
                RxHttp.postJson("https://api.ilearn.enjoycolin.top/login"),
                true,
                "login failed, password incorrect",
                p -> p.asResponse(TokenResponse.class)
        );
        accountBox = ObjectBox.get().boxFor(Account.class);
        List<Account> accounts = accountBox.getAll();
        if (accounts != null && accounts.size() > 0) {
            // restore account info
            Account account = accounts.get(0);
            loginParams = new HashMap<String, Object>(){{
                put("username", account.getUsername());
                put("password", account.getPassword());
            }};
            tokenValue = account.getToken();
        }
    }

    private static Backend instance = new Backend();
    public static Backend getInst() {
        return instance;
    }

    protected static int successCode = 0;

    @Override
    protected void onRefreshSuccess(Object response) {
        synchronized (tokenValue) {
            tokenValue = ((TokenResponse) response).getToken();
        }
    }

    public void login(String username, String password, Handler handler) {
        loginParams = new HashMap<String, Object>(){{
            put("username", username);
            put("password", password);
        }};
        asyncRefresh(handler);
    }

    class RegisterCallbackHandler extends Handler {
        Handler originalHandler;
        String username;
        String password;

        public RegisterCallbackHandler(Handler originalHandler,
                                       String username, String password) {
            this.originalHandler = originalHandler;
            this.username = username;
            this.password = password;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0 && msg.obj != null) {
                // store account info
                tokenValue = ((TokenResponse) msg.obj).getToken();
                loginParams = new HashMap<String, Object>(){{
                    put("username", username);
                    put("password", password);
                }};
                // store account info into db
                accountBox.removeAll();
                Account account = new Account()
                        .setToken(tokenValue)
                        .setUsername(username)
                        .setPassword(password);
                accountBox.put(account);
                msg.obj = account;  // send account to frontend
            }
            Message.obtain(originalHandler, msg.what, msg.obj);
        }
    }

    public void register(String email, String username,
                            String password, Handler handler) {
        POSTJson("/register",
                new HashMap<String, Object>(){{
                    put("email", email);
                    put("username", username);
                    put("password", password);
                }},
                p -> p.asResponse(TokenResponse.class),
                new RegisterCallbackHandler(handler, username, password)
        );
    }

    public void changePassword(String oldPassword, String newPassword,
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

    public void checkUsername(String username, Handler handler) {
        POSTJson("/users/checkbyname",
                new HashMap<String, Object>(){{
                    put("username", username);
                }},
                p -> p.asString(),
                handler);
    }

    public void checkPassword(String password, Handler handler) {

    }
    // TODO reset password by email? ask TA
}
