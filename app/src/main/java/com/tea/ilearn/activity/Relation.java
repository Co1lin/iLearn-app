package com.tea.ilearn.activity;

public class Relation {

    int dir; // 0 for left, 1 for right
    String type;
    String name;

    public Relation(int dir, String type, String name) {
        this.dir = dir;
        this.type = type;
        this.name = name;
    }
}
