package com.tea.ilearn.net.edukg;

import android.os.Handler;

import com.tea.ilearn.Constant;
import com.tea.ilearn.net.APIRequest;

import java.util.HashMap;

import rxhttp.wrapper.param.RxHttp;

/**
 * Network communication to EduKG
 */
public class EduKG extends APIRequest {
    private EduKG() {
        super(
                "http://open.edukg.cn/opedukg/api",
                "/typeAuth/user/login",
                "/typeOpen/open",
                new HashMap<String, Object>(){{
                    put("phone", "153111551");
                    put("password", "abc123456");
                }},
                "id",
                RxHttp.postForm("http://open.edukg.cn/opedukg/api/typeAuth/user/login"),
                false,
                "请先登录",
                p -> p.asClass(LoginResponse.class)
        );
    }
    private static EduKG instance = new EduKG();
    public static EduKG getInst() {
        return instance;
    }

    protected static int successCode = 0;

    @Override
    protected void onRefreshSuccess(Object response) {
        synchronized (tokenValue) {
            tokenValue = ((LoginResponse) response).id;
        }
    }

    public void fuzzySearchEntityWithCourse(
            String course, String searchKey, Handler handler
    ) {
        GET("/instanceList",
            new HashMap<String, Object>() {{
                put("course", course);
                put("searchKey", searchKey);
            }},
            p -> p.asEduKGResponseList(Entity.class),
            handler
        );
    }

    public void fuzzySearchEntityWithAllCourse(String searchKey, Handler handler) {
        for (String subject: Constant.EduKG.SUBJECTS_EN) {
            fuzzySearchEntityWithCourse(subject, searchKey, handler);
        }
    }

    public void getEntityDetails(String course, String entityName, Handler handler) {
        GET("/infoByInstanceName",
            new HashMap<String, Object>() {{
                put("course", course);
                put("name", entityName);
            }},
            p -> p.asEduKGResponse(EduKGEntityDetail.class),
            handler
        );
    }

    public void getProblems(String keyword, Handler handler) {
        GET("/questionListByUriName",
                new HashMap<String, Object>() {{
                    put("uriName", keyword);
                }},
                p -> p.asEduKGResponseList(Problem.class),
                handler
        );
    }

    public void qAWithSubject(String course, String question, Handler handler) {
        POSTForm("/inputQuestion",
                new HashMap<String, Object>() {{
                    put("course", course);
                    put("inputQuestion", question);
                }},
                p -> p.asEduKGResponseList(Answer.class),
                handler
        );
    }

    public void qAWithAllSubjects(String question, Handler handler) {
        for (String subject: Constant.EduKG.SUBJECTS_EN) {
            qAWithSubject(subject, question, handler);
        }
    }

    public void getNamedEntities(String course, String queryString, Handler handler) {
        POSTForm("/linkInstance",
            new HashMap<String, Object>(){{
                put("course", course);
                put("context", queryString);
            }},
            p -> p.asEduKGResponse(LinkResults.class),
            handler
        );
    }

}