package com.tea.ilearn.net.edukg;

import com.google.gson.annotations.SerializedName;

public class EduKGRelation {
    String predicate;
    @SerializedName("predicate_label")
    String predicateLabel;
    String object;
    @SerializedName("object_label")
    String objectLabel;
    String subject;
    @SerializedName("subject_label")
    String subjectLabel;

    public String getPredicateLabel() {
        return predicateLabel;
    }

    public String getObjectLabel() {
        if (object == null) return subjectLabel;
        else return objectLabel;
    }

    public int getDirection() {
        if (object == null) return 1;
        else return 0;
    }
}
