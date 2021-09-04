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

    public Info() {}

    public ArrayList<String> getCategories() {
        return categories;
    }

    public String getCategory() {
        return String.join(" ", categories);
    }

    public String getName() {
        return name;
    }

    public int getKd() {
        return kd;
    }

    public String getSubject() {
        return subject;
    }

    public String getId() {
        return id;
    }

    public boolean isStar() {
        return star;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public Info setKd(int kd) {
        this.kd = kd;
        return this;
    }

    public Info setName(String name) {
        this.name = name;
        return this;
    }

    public Info setSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public Info setId(String id) {
        this.id = id;
        return this;
    }

    public Info setStar(boolean star) {
        this.star = star;
        return this;
    }

    public Info setLoaded(boolean loaded) {
        this.loaded = loaded;
        return this;
    }

    public Info setCategories(ArrayList<String> categories) {
        this.categories = categories;
        return this;
    }
}
