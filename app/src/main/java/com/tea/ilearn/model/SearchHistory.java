package com.tea.ilearn.model;

import com.tea.ilearn.net.edukg.EduKGEntityDetail;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Unique;
import io.objectbox.relation.ToMany;

@Entity
public class SearchHistory {
    @Id
    public long id;

    @Index
    @Unique
    public String keyword;

    @Backlink(to = "searchHistories")
    public ToMany<EduKGEntityDetail> entities;

    public SearchHistory(long id, String keyword, ToMany<EduKGEntityDetail> entities) {
        this.id = id;
        this.keyword = keyword;
        this.entities = entities;
    }

    public SearchHistory() {}

    public SearchHistory(String keyword) {
        this.keyword = keyword;
    }
}

