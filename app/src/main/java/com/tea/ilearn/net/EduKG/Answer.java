package com.tea.ilearn.net.EduKG;

import com.google.gson.annotations.SerializedName;

public class Answer {
    String value;       // answer
    @SerializedName("subject")
    String entity;
    @SerializedName("subjectUri")
    String entityUri;
    @SerializedName("predicate")
    String relatedProperty;
}
