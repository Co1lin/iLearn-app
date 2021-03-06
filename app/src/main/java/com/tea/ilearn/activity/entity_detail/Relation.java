package com.tea.ilearn.activity.entity_detail;

import java.util.ArrayList;

public class Relation {

    int dir; // 0 for left, 1 for right, 2 for property
    String type;
    String name;
    String subject;
    String category;
    String uri;
    ArrayList<String> categories;

    public Relation(String type, String name, int dir) {
        this.dir = dir;
        this.type = type;
        this.name = name;
    }

    public Relation(String type, String name, int dir, String subject,
                    String category, ArrayList<String> categories, String uri) {
        this.dir = dir;
        this.type = type;
        this.name = name;
        this.subject = subject;
        this.category = category;
        this.uri = uri;
        this.categories = categories;
    }
}
