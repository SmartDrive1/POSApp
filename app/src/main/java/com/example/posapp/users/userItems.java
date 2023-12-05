package com.example.posapp.users;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class userItems {
    String id;
    String fullName;
    String userName;
    String password;
    String access;
    String userImgBase64;

    public userItems(String id, String fullName, String userName, String password, String access, String userImgBase64) {
        this.id = id;
        this.fullName = fullName;
        this.userName = userName;
        this.password = password;
        this.access = access;
        this.userImgBase64 = userImgBase64;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getUserImgBase64() {
        return userImgBase64;
    }
}
