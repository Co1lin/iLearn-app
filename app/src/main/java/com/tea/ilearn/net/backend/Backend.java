package com.tea.ilearn.net.backend;

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

                }},
                "",
                "",
                LoginResponse.class
        );

    }

    @Override
    protected void onRefreshSuccess(Object response) {

    }


}
