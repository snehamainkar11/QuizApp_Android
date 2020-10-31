package com.example.myapplication;


import android.net.Uri;

public class User{
    String name;
    String userName;
    String password;
    String confirmPassword;
    String contact;
    String url;

    public User(String name, String userName, String password, String contact, String url) {

        this.name = name;
        this.userName = userName;
        this.password = password;
        this.contact = contact;
        this.url=url;
    }

    public User() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }


}
