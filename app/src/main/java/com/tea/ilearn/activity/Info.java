package com.tea.ilearn.activity;

class Info {
    int kd; // 0 for entity
    String name, category, subject;
    boolean star, loaded;

    public Info(int kd, String name, String subject, boolean star, boolean loaded, String category) {
        this.kd = kd;
        this.name = name;
        this.subject = subject;
        this.star = star;
        this.loaded = loaded;
        this.category = category;
    }
}
