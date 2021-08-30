package com.tea.ilearn.ui.home;

class Info {
    int kd; // 0 for entity
    String name, category, subject, id;
    boolean star, loaded;

    public Info(int kd, String name, String subject, boolean star, boolean loaded, String category, String id) {
        this.kd = kd;
        this.name = name;
        this.subject = subject;
        this.star = star;
        this.loaded = loaded;
        this.category = category;
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }
}
