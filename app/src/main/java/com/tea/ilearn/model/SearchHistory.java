package com.tea.ilearn.model;

import com.google.gson.annotations.SerializedName;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

@Entity
public class SearchHistory {
    @Id
    public long id;

    @Index @SerializedName("description")
    String keyword;

    public SearchHistory(long id, String keyword) {
        this.id = id;
        this.keyword = keyword;
    }

    public SearchHistory() {}

    public String getKeyword() {
        return keyword;
    }

    public SearchHistory setKeyword(String keyword) {
        this.keyword = keyword;
        return this;
    }
}

