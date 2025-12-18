package com.example.chatapplication.Utils;

import android.content.Context;
import android.util.Log;
import com.example.chatapplication.R;
import com.google.auth.oauth2.GoogleCredentials;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FCMTokenUtil {
    private static final String TAG = "FCMUtils";
    private static final String FCM_URL = "https://fcm.googleapis.com/v1/projects/easy-chat-backend-ad899/messages:send";
    private static final String SCOPES = "https://www.googleapis.com/auth/firebase.messaging";
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private static final OkHttpClient client = new OkHttpClient();

    /**
     * Sends a push notification to a recipient using FCM.
     * @param context Application context
     * @param recipientToken FCM token of the recipient
     * @param title Notification title
     * @param body Notification body
     * @param senderId ID of the sender
     * @param senderName Name of the sender
     */
    public static void sendNotification(Context context, String recipientToken, String title, String body,
                                        String senderId, String senderName) {
        executor.execute(() -> {
            try {
                String accessToken = getAccessToken(context);
                if (accessToken != null) {
                    sendNotificationRequest(accessToken, recipientToken, title, body, senderId, senderName);
                } else {
                    Log.e(TAG, "Failed to get access token");
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to send notification", e);
            }
        });
    }

    private static String getAccessToken(Context context) {
        try {
            String serviceAccountJson = AndroidUtil.getServiceAccountFromEncryptedPrefs(context);
            if (serviceAccountJson == null) {
                Log.e(TAG, "Service account JSON not found in encrypted storage");
                return null;
            }

            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(new ByteArrayInputStream(serviceAccountJson.getBytes()))
                    .createScoped(Collections.singletonList(SCOPES));
            credentials.refreshIfExpired();
            String token = credentials.getAccessToken().getTokenValue();
            Log.d(TAG, "Access token obtained successfully");
            return token;
        } catch (IOException e) {
            Log.e(TAG, "Failed to get access token from encrypted storage", e);
            return null;
        }
    }

    private static void sendNotificationRequest(String accessToken, String recipientToken,
                                                String title, String body, String senderId, String senderName) {
        try {
            Log.d(TAG, "Sending notification request...");
            Log.d(TAG, "Recipient token: " + recipientToken);
            Log.d(TAG, "Title: " + title + ", Body: " + body);

            JSONObject message = new JSONObject();
            JSONObject notification = new JSONObject();
            JSONObject data = new JSONObject();

            notification.put("title", title);
            notification.put("body", body);

            data.put("title", title);
            data.put("body", body);
            data.put("userId", senderId);
            data.put("senderName", senderName);
            data.put("click_action", "OPEN_CHAT");
            message.put("token", recipientToken);
            message.put("notification", notification);
            message.put("data", data);

            JSONObject fcmMessage = new JSONObject();
            fcmMessage.put("message", message);

            RequestBody requestBody = RequestBody.create(
                    fcmMessage.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(FCM_URL)
                    .post(requestBody)
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                Log.d(TAG, "Notification sent successfully!");
            } else {
                String responseBody = response.body() != null ? response.body().string() : "No response body";
                Log.e(TAG, "Failed to send notification. Response code: " + response.code());
                Log.e(TAG, "Response body: " + responseBody);
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON error while creating notification request", e);
        } catch (IOException e) {
            Log.e(TAG, "Network error while sending notification", e);
        } catch (Exception e) {
            Log.e(TAG, "Unexpected error while sending notification", e);
        }
    }

    // Get FCM token and save to user profile
    public static void initializeFCM(Context context) {
        // First, save the service account JSON to encrypted storage
        boolean saved = AndroidUtil.saveServiceAccountToEncryptedPrefs(context, R.raw.easy_chat_backend_ad899_6738c1a24470);
        if (!saved) {
            Log.e(TAG, "Failed to initialize FCM due to service account saving error");
            return;
        }

        com.google.firebase.messaging.FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d(TAG, "FCM Registration Token: " + token);

                    // Save token to user profile
                    if (FirebaseUtils.isLoggedIn()) {
                        FirebaseUtils.currentUserDetails()
                                .update("fcmToken", token)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Log.d(TAG, "FCM token saved to user profile successfully");
                                    } else {
                                        Log.e(TAG, "Failed to save FCM token to user profile", updateTask.getException());
                                    }
                                });
                    }
                });
    }


}