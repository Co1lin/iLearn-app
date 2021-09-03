package com.tea.ilearn.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Unique;

@Entity
public class Account {
    @Id
    public long id;

    @Unique
    String username;
    String password;
    String token;

    public Account setUsername(String username) {
        this.username = username;
        return this;
    }

    public Account setPassword(String password) {
        this.password = password;
        return this;
    }

    public Account setToken(String token) {
        this.token = token;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getToken() {
        return token;
    }
}
