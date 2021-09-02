package com.tea.ilearn.model;

import com.tea.ilearn.net.edukg.EduKGEntityDetail;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToMany;

@Entity
public class Category {
    @Id
    public long id;

    @Index @Unique
    String name;

    int num;

    public ToMany<EduKGEntityDetail> entities;

    public String getName() {
        return name;
    }

    public int getNum() {
        return num;
    }

    public ToMany<EduKGEntityDetail> getEntities() {
        return entities;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public void increaseNum() {
        num++;
    }
}
