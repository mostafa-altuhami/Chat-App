package com.example.chatapplication.Utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.chatapplication.Model.UserModel;

import java.io.InputStream;
import java.util.Scanner;

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

    /**
     * Saves the service account JSON to EncryptedSharedPreferences.
     * @param context Application context
     * @param rawResourceId Resource ID of the service account JSON file
     * @return true if saved successfully, false otherwise
     */
    public static boolean saveServiceAccountToEncryptedPrefs(Context context, int rawResourceId) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            SharedPreferences prefs = EncryptedSharedPreferences.create(
                    "secure_prefs",
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );

            // Check if already saved
            if (prefs.contains("service_account_json")) {
                return true;
            }

            // Read JSON from raw resource
            InputStream inputStream = context.getResources().openRawResource(rawResourceId);
            String serviceAccountJson = new Scanner(inputStream).useDelimiter("\\A").next();
            inputStream.close();

            // Save to EncryptedSharedPreferences
            prefs.edit().putString("service_account_json", serviceAccountJson).apply();
            return true;
        } catch (Exception e) {
            AndroidUtil.showToast(context, "Failed to save service account");
            return false;
        }
    }

    /**
     * Retrieves the service account JSON from EncryptedSharedPreferences.
     * @param context Application context
     * @return The service account JSON string or null if not found
     */
    public static String getServiceAccountFromEncryptedPrefs(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            SharedPreferences prefs = EncryptedSharedPreferences.create(
                    "secure_prefs",
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            return prefs.getString("service_account_json", null);
        } catch (Exception e) {
            AndroidUtil.showToast(context, "Failed to retrieve service account");
            return null;
        }
    }
}
