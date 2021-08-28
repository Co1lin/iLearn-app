package com.tea.ilearn.net.EduKG;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EntityDetail {
    public class Property {
        String predicate;
        //@SerializedName("predicateLabel")
        String predicateLabel;
        String object;

        public String getPredicateLabel() { return predicateLabel; }

        public String getObject() { return object; }
    }
    @SerializedName("property")
    ArrayList<Property> properties = new ArrayList<>();
    String label;
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
