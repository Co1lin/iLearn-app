package com.tea.ilearn.model;

import com.google.gson.annotations.SerializedName;
import com.tea.ilearn.net.edukg.EduKGEntityDetail;

import io.objectbox.annotation.Backlink;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.relation.ToMany;

@Entity
public class SearchHistory {
    @Id
    public long id;

    @Index @SerializedName("description")
    String keyword;

    @Index
    String subject;

    @Backlink(to = "searchHistories")
    public ToMany<EduKGEntityDetail> entities;

    public SearchHistory(long id, String keyword, ToMany<EduKGEntityDetail> entities) {
        this.id = id;
        this.keyword = keyword;
        this.entities = entities;
    }

    public SearchHistory() {}

    public String getKeyword() {
        return keyword;
    }

    public String getSubject() {
        return subject;
    }

    public SearchHistory setKeyword(String keyword) {
        this.keyword = keyword;
        return this;
    }

    public SearchHistory setSubject(String subject) {
        this.subject = subject;
        return this;
    }
}

