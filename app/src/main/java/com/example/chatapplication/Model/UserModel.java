package com.example.chatapplication.Model;

import com.google.firebase.Timestamp;

// model class for user
public class UserModel {
    private String phone;
    private String username;
    private Timestamp timestamp;
    private String userId;
    private String imageUrl;
    private String fcmToken;
    public UserModel() {
    }


    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public UserModel(String phone, String username, Timestamp timestamp, String userId) {
        this.phone = phone;
        this.username = username;
        this.timestamp = timestamp;
        this.userId = userId;

    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
