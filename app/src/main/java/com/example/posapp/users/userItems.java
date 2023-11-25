package com.example.posapp.users;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class userItems {
    String id;
    String fullName;
    String userName;
    String password;
    String access;
    byte[] userImg;

    public Bitmap getUserImageBitmap() {
        if (userImg != null) {
            return BitmapFactory.decodeByteArray(userImg, 0, userImg.length);
        } else {
            return null; // Handle the case when prodImage is null or empty
        }
    }

    public userItems(String id, String fullName, String userName, String password, String access, byte[] userImg) {
        this.id = id;
        this.fullName = fullName;
        this.userName = userName;
        this.password = password;
        this.access = access;
        this.userImg = userImg;
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

    public byte[] getUserImg() {
        return userImg;
    }

    public void setUserImg(byte[] userImg) {
        this.userImg = userImg;
    }
}
