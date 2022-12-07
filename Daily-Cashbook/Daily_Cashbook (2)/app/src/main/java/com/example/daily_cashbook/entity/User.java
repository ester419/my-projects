package com.example.daily_cashbook.entity;

public class User {
    private int id;
    private String userName;
    private String password;
    private String email;
    private String tel;
    private String profile;
    private String icon;

    public User(int id, String userName, String password, String email, String tel, String profile) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.tel = tel;
        this.profile = profile;
    }

    public User() {

    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return this.userName;
    }
}
