package com.tea.ilearn.net.EduKG;

import com.google.gson.annotations.SerializedName;

public class Answer {
    @SerializedName("value")
    String answer;
    @SerializedName("subject")
    String entity;
    @SerializedName("subjectUri")
    String entityUri;
    @SerializedName("predicate")
    String relatedProperty;

    public String getAnswer() {
        return answer;
    }

    public String getEntity() {
        return entity;
    }

    public String getEntityUri() {
        return entityUri;
    }

    public String getRelatedProperty() {
        return relatedProperty;
    }
}
