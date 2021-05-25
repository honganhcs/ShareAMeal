package com.example.shareameal;

public class User {
    private String userGroup;
    private String name;

    public User() {}

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public String getName() {
        return name;
    }
}
