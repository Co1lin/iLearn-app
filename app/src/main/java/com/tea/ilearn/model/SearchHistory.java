package com.tea.ilearn.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Unique;

@Entity
public class SearchHistory {
    @Id
    public long id;

    @Index
    @Unique
    public String keyword;

//    @Backlink(to = "searchHistories")
//    public ToMany<EduKGEntityDetail> entities;
}

