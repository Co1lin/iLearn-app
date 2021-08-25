package com.tea.ilearn.net;

import rxhttp.wrapper.annotation.DefaultDomain;
import rxhttp.wrapper.annotation.Domain;

public class Url {
    @DefaultDomain()
    public static String baseUrl = "https://api.ilearn.enjoycolin.top";

    @Domain(name = "EduKGLogin")
    public static String eduKGLoginUrl = "http://open.edukg.cn/opedukg/api/typeAuth/user/login";

    @Domain(name = "EduKGOpen")
    public static String eduKGOpenUrl = "http://open.edukg.cn/opedukg/api/typeOpen/open";
}

