package com.tea.ilearn.ui.home;

import java.util.ArrayList;

class Info {
    int kd; // 0 for entity
    String name, subject, id;
    boolean star, loaded;
    ArrayList<String> categories;

    public Info(int kd, String name, String subject, boolean star,
                boolean loaded, String id, ArrayList<String> categories) {
        this.kd = kd;
        this.name = name;
        this.subject = subject;
        this.star = star;
        this.loaded = loaded;
        this.categories = categories;
        this.id = id;
    }

    public ArrayList<String> getCategories() {
        return categories;
    }

    public String getCategory() {
        return String.join(" ", categories);
    }

    public String getName() {
        return name;
    }
}
