package com.tea.ilearn.model;

import com.google.gson.annotations.SerializedName;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Unique;

@Entity
public class SearchHistory {
    @Id
    public long id;

    @Index @SerializedName("description") @Unique
    String keyword;

    long timestamp = 0;

    public SearchHistory() {}

    public String getKeyword() {
        return keyword;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public SearchHistory setKeyword(String keyword) {
        this.keyword = keyword;
        return this;
    }

    public SearchHistory setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }
}

