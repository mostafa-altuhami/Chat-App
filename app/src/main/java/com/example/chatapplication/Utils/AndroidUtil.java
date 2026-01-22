package com.example.chatapplication.Utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.example.chatapplication.Model.UserModel;

public class AndroidUtil {

    // fun shows a toast
    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    // fun to pass data to a specific intent
    public static void passUserModelAsIntent(Intent intent, UserModel userModel) {
        intent.putExtra("username", userModel.getUsername());
        intent.putExtra("phone", userModel.getPhone());
        intent.putExtra("userId", userModel.getUserId());
        intent.putExtra("imageUrl", userModel.getImageUrl());
        intent.putExtra("fcmToken", userModel.getFcmToken());
    }

    // fun to get data from a specific intent
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
