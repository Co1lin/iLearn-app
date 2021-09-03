package com.tea.ilearn.model;

import java.util.ArrayList;
import java.util.Date;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

@Entity
public class UserStatistics {
    @Id
    public long id;

    Date firstDate;


    ArrayList<Integer> entitiesViewed = new ArrayList<>();

    public UserStatistics setFirstDate(Date firstDate) {
        this.firstDate = firstDate;
        return this;
    }

    public UserStatistics setEntitiesViewed(ArrayList<Integer> entitiesViewed) {
        this.entitiesViewed = entitiesViewed;
        return this;
    }

    public Date getFirstDate() {
        return firstDate;
    }

    public ArrayList<Integer> getEntitiesViewed() {
        return entitiesViewed;
    }
}
