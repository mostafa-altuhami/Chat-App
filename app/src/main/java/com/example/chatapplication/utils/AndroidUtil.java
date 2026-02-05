package com.example.chatapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.example.chatapplication.data.model.UserModel;

public class AndroidUtil {

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void passUserModelAsIntent(Intent intent, UserModel userModel) {
        intent.putExtra("username", userModel.getUsername());
        intent.putExtra("phone", userModel.getPhone());
        intent.putExtra("userId", userModel.getUserId());
        intent.putExtra("imageUrl", userModel.getImageUrl());
        intent.putExtra("fcmToken", userModel.getFcmToken());
    }

    public static UserModel getUserModelFromIntent(Intent intent) {
        UserModel model = new UserModel();
        model.setUsername(intent.getStringExtra("username"));
        model.setPhone(intent.getStringExtra("phone"));
        model.setUserId(intent.getStringExtra("userId"));
        model.setImageUrl(intent.getStringExtra("imageUrl"));
        model.setFcmToken(intent.getStringExtra("fcmToken"));
        return model;
    }



}
