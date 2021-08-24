package com.tea.ilearn.net.EduKG;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class EntityDetail {
    public class Property {
        String predicate;
        //@SerializedName("predicateLabel")
        String predicateLabel;
        String object;
    }
    @SerializedName("property")
    ArrayList<Property> properties = new ArrayList<>();
    String label;
    public class Relation {
        String predicate;
        String predicateLabel;
        String object;
        String objectLabel;
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
