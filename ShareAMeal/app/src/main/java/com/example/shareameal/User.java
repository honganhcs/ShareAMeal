package com.example.shareameal;

import com.google.firebase.database.Query;

public class User {
    private String userGroup;
    private String name;
    private String restaurant;
    private String address;
    private String userId;

    public User() {}

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRestaurant(String restaurant) { this.restaurant = restaurant; }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public String getName() {
        return name;
    }

    public String getRestaurant() { return restaurant;}

    public String getAddress() {
        return address;
    }

    public String getUserId() {
        return userId;
    }
}
