package com.softdesign.devintensive.data.network.restmodels;

@SuppressWarnings("unused")
public class UsedData {

    private User user;
    private String token;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}