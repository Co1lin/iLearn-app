package com.tea.ilearn.net.edukg;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EntityDetail {
    public class Property {
        String predicate;
        String predicateLabel;
        String object;

        public String getPredicateLabel() { return predicateLabel; }

        public String getObject() { return object; }
    }

    public class Relation {
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

    @SerializedName("content")
    ArrayList<Relation> relations = new ArrayList<>();
    @SerializedName("property")
    ArrayList<Property> properties = new ArrayList<>();
    String label;
    String course;
    String category;
    String uri;
    boolean stared;
    boolean viewed;

    public String getCourse() {
        return course;
    }

    public String getCategory() {
        return category;
    }

    public String getUri() {
        return uri;
    }

    public boolean isStared() {
        return stared;
    }

    public boolean isViewed() {
        return viewed;
    }

    public EntityDetail setCourse(String course) {
        this.course = course;
        return this;
    }

    public EntityDetail setCategory(String category) {
        this.category = category;
        return this;
    }

    public EntityDetail setStared(boolean stared) {
        this.stared = stared;
        return this;
    }

    public EntityDetail setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public ArrayList<Property> getProperties() {
        return properties;
    }

    public String getLabel() {
        return label;
    }

    public ArrayList<Relation> getRelations() {
        return relations;
    }
}
