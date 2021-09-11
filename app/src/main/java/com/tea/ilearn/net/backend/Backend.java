package com.tea.ilearn.net.backend;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.tea.ilearn.model.Account;
import com.tea.ilearn.model.Preference;
import com.tea.ilearn.model.SearchHistory;
import com.tea.ilearn.model.UserStatistics;
import com.tea.ilearn.net.APIRequest;
import com.tea.ilearn.net.edukg.EduKGEntityDetail;
import com.tea.ilearn.utils.ObjectBox;

import java.util.ArrayList;
import java.util.Arrays;
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
                "runtime exception",
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

    class LogInCallbackHandler extends Handler {
        Handler originalHandler;
        String username;
        String password;

        public LogInCallbackHandler(Handler originalHandler,
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
            if (originalHandler != null)
                Message.obtain(originalHandler, msg.what, msg.obj).sendToTarget();
            getPersonalData(null);
        }
    }

    public void login(String username, String password, Handler handler) {
        loginParams = new HashMap<String, Object>(){{
            put("username", username);
            put("password", password);
        }};
        asyncRefresh(new LogInCallbackHandler(handler, username, password));
    }

    public void login(Handler handler) {
        asyncRefresh(new LogInCallbackHandler(handler,
                (String) loginParams.get("username"),
                (String) loginParams.get("password"))
        );
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
                new LogInCallbackHandler(handler, username, password)
        );
    }

    public void changePassword(String oldPassword, String newPassword, Handler handler) {
        Handler callbackHandler = new ChangePasswordCallback(handler).setPassword(newPassword);
        POSTJson("/users/changepassword",
                new HashMap<String, Object>(){{
                    put("new_password", newPassword);
                    put("old_password", oldPassword);
                }},
                p -> p.asResponse(String.class),
                callbackHandler
        );
    }

    static class ChangePasswordCallback extends Handler {
        Handler originalHandler;
        String password;

        public ChangePasswordCallback setPassword(String password) {
            this.password = password;
            return this;
        }

        public ChangePasswordCallback(Handler originalHandler) {
            this.originalHandler = originalHandler;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1 || msg.obj == null) {
                Log.e("Backend/ChangePasswordCallback", msg.what + " " + msg.obj);
                if (originalHandler != null)
                    Message.obtain(originalHandler, 1, msg.obj).sendToTarget();
            }
            else {  // store to DB and send to frontend
                Box<Account> accountBox = ObjectBox.get().boxFor(Account.class);
                List<Account> res = accountBox.getAll();
                if (res != null && res.size() > 0) {
                    Account account = res.get(0);
                    account.setPassword(password);
                    accountBox.put(account);
                    if (originalHandler != null)
                        Message.obtain(originalHandler,0, account).sendToTarget();
                }
            }
        }
    }

    public void resetPassword(String username, String code, String newPassword, Handler handler) {
        POSTJson("/users/reset",
                new HashMap<String, Object>(){{
                    put("username", username);
                    put("code", code);
                    put("new_password", newPassword);
                }},
                p -> p.asResponse(String.class),
                handler);
    }

    public void forgetPassword(String username, Handler handler) {
        POSTJson("/users/forget",
                new HashMap<String, Object>(){{
                    put("username", username);
                }},
                p -> p.asResponse(String.class),
                handler);
    }

    public void checkUsername(String username, Handler handler) {
        POSTJson("/users/checkbyname",
                new HashMap<String, Object>(){{
                    put("username", username);
                }},
                p -> p.asResponse(String.class),
                handler);
    }

    public void checkPassword(String password, Handler handler) {
        POSTJson("/users/checkpassword",
                new HashMap<String, Object>(){{
                    put("password", password);
                }},
                p -> p.asResponse(String.class),
                handler);
    }
    // TODO reset password by email? ask TA

    // personal data
    public void getPersonalData(Handler handler) {
        // Handler callbackHandler = new GetPersonalDataCallback(handler);
        // getUserStatistics(callbackHandler);
        getSearchHistories(null);
        getEntities(null);
        getPreferences(null);
    }

    static class GetPersonalDataCallback extends Handler {
        Handler originalHandler;

        public GetPersonalDataCallback(Handler originalHandler) {
            this.originalHandler = originalHandler;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

        }
    }

    // user statistics
    public void uploadUserStatistics(Handler handler) {
        new Thread(() -> {
            Box<UserStatistics> userStatisticsBox = ObjectBox.get().boxFor(UserStatistics.class);
            List<UserStatistics> res = userStatisticsBox.getAll();
            if (res != null && res.size() > 0) {
                uploadUserStatistics(res.get(0), handler);
            }
        }).start();
    }

    public void uploadUserStatistics(UserStatistics statistics, Handler handler) {
        POSTJson("/users/viewed",
                new HashMap<String, Object>(){{
                    put("first_date", statistics.getFirstDate());
                    put("entities_viewed", statistics.getEntitiesViewed());
                }},
                p -> p.asResponse(String.class),
                handler);
    }

    public void getUserStatistics(Handler handler) {
        Handler callbackHandler = new GetUserStatisticsCallback(handler);
        GET("/users/viewed",
                new HashMap<>(),
                p -> p.asResponse(UserStatistics.class),
                callbackHandler);
    }

    static class GetUserStatisticsCallback extends Handler {
        Handler originalHandler;

        public GetUserStatisticsCallback(Handler originalHandler) {
            this.originalHandler = originalHandler;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1 || msg.obj == null) {
                Log.e("Backend/GetUserStatisticsCallback", msg.what + " " + msg.obj);
                if (originalHandler != null)
                    Message.obtain(originalHandler, 1, msg.obj).sendToTarget();
            }
            else {  // store to DB and send to frontend
                UserStatistics statistics = (UserStatistics) msg.obj;
                Box<UserStatistics> statisticsBox = ObjectBox.get().boxFor(UserStatistics.class);
                statisticsBox.removeAll();
                statisticsBox.put(statistics);
                if (originalHandler != null)
                    Message.obtain(originalHandler,0, statistics).sendToTarget();
            }
        }
    }

    // preferences
    public void uploadPreferences(Handler handler) {
        new Thread(() -> {
            Box<Preference> preferenceBox = ObjectBox.get().boxFor(Preference.class);
            List<Preference> res = preferenceBox.getAll();
            if (res != null && res.size() > 0) {
                uploadPreferences(res.get(0), handler);
            }
        }).start();
    }

    public void uploadPreferences(Preference preference, Handler handler) {
        POSTJson("/preference",
                new HashMap<String, Object>(){{
                    put("subjects", preference.getSubjects());
                }},
                p -> p.asResponse(String.class),
                handler);
    }

    public void getPreferences(Handler handler) {
        Handler callbackHandler = new GetPreferencesCallback(handler);
        GET("/preference",
                new HashMap<>(),
                p -> p.asResponse(Preference.class),
                callbackHandler);
    }

    static class GetPreferencesCallback extends Handler {
        Handler originalHandler;

        public GetPreferencesCallback(Handler originalHandler) {
            this.originalHandler = originalHandler;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1 || msg.obj == null) {
                Log.e("Backend/GetPreferencesCallback", msg.what + " " + msg.obj);
                if (originalHandler != null)
                    Message.obtain(originalHandler, 1, msg.obj).sendToTarget();
            }
            else {  // store to DB and send to frontend
                Preference preference = (Preference) msg.obj;
                Box<Preference> preferenceBox = ObjectBox.get().boxFor(Preference.class);
                preferenceBox.removeAll();
                preferenceBox.put(preference);
                if (originalHandler != null)
                    Message.obtain(originalHandler,0, preference).sendToTarget();
            }
        }
    }

    // search histories
    public void uploadSearchHistory(SearchHistory history, Handler handler) {
        PUTJson("/history/put",
                new HashMap<String, Object>() {{
                    put("description", history.getKeyword());
                    put("timestamp", history.getTimestamp());
                }},
                p -> p.asResponse(String.class),
                handler);
    }

    public void getSearchHistories(Handler handler) {
        Handler callbackHandler = new GetSearchHistoriesCallback(handler);
        GET("/history/get",
                new HashMap<>(),
                p -> p.asResponseList(SearchHistory.class),
                callbackHandler);
    }

    static class GetSearchHistoriesCallback extends Handler {
        Handler originalHandler;

        public GetSearchHistoriesCallback(Handler originalHandler) {
            this.originalHandler = originalHandler;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1 || msg.obj == null) {
                Log.e("Backend/GetSearchHistoriesCallback", msg.what + " " + msg.obj);
            }
            else {
                ArrayList<SearchHistory> histories = (ArrayList<SearchHistory>) msg.obj;
                Box<SearchHistory> historyBox = ObjectBox.get().boxFor(SearchHistory.class);
                historyBox.removeAll();
                historyBox.put(histories);
            }
            if (originalHandler != null)
                Message.obtain(originalHandler, msg.what, msg.obj).sendToTarget();
        }
    }

    public void deleteSearchHistory(SearchHistory history, Handler handler) {
        PUTJson("/history/delete",
                new HashMap<String, Object>() {{
                    put("histories", Arrays.asList(history.getKeyword()));
                }},
                p -> p.asResponse(String.class),
                handler);
    }

    // "viewed" entities
    public void uploadEntity(EduKGEntityDetail entityDetail, Handler handler) {
        POSTJson("/entity",
                new HashMap<String, Object>(){{
                    put("uri", entityDetail.getUri());
                    put("label", entityDetail.getLabel());
                    put("subject", entityDetail.getSubject());
                    put("starred", entityDetail.isStarred());
                    put("viewed", entityDetail.isViewed());
                    put("category_list", entityDetail.getCategories());
                }},
                p -> p.asResponse(String.class),
                handler);
    }

    public void getEntities(Handler handler) {
        Handler callbackHandler = new GetEntitiesCallback(handler);
        GET("/entity",
                new HashMap<>(),
                p -> p.asResponseList(EduKGEntityDetail.class),
                callbackHandler);
    }

    static class GetEntitiesCallback extends Handler {
        Handler originalHandler;

        public GetEntitiesCallback(Handler originalHandler) {
            this.originalHandler = originalHandler;
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1 || msg.obj == null) {
                Log.e("Backend/GetEntitiesCallback", msg.what + " " + msg.obj);
            }
            else {
                ArrayList<EduKGEntityDetail> entityDetails = (ArrayList<EduKGEntityDetail>) msg.obj;
                Box<EduKGEntityDetail> detailsBox = ObjectBox.get().boxFor(EduKGEntityDetail.class);
                detailsBox.removeAll();
                detailsBox.put(entityDetails);
            }
            if (originalHandler != null)
                Message.obtain(originalHandler, msg.what, msg.obj).sendToTarget();
        }
    }

    public void getRecommendedEntities(Handler handler) {
        GET("/question",
                new HashMap<>(),
                p -> p.asResponseList(String.class),
                handler);
    }

    public void logout() {
        new Thread(() -> {
            loginParams = new HashMap<String, Object>(){{
                put("username", "");
                put("password", "");
            }};
            tokenValue = "";
            accountBox.removeAll();
            ObjectBox.get().boxFor(SearchHistory.class).removeAll();
            ObjectBox.get().boxFor(UserStatistics.class).removeAll();
            ObjectBox.get().boxFor(EduKGEntityDetail.class).removeAll();
        }).start();
    }
}
