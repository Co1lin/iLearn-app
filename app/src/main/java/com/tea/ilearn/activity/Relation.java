package com.tea.ilearn.activity;

public class Relation {

    int dir; // 0 for left, 1 for right, 2 for property
    String type;
    String name;

    public Relation(String type, String name, int dir) {
        this.dir = dir;
        this.type = type;
        this.name = name;
    }
}
