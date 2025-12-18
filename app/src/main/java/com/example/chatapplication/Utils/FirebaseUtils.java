package com.example.chatapplication.Utils;

import android.annotation.SuppressLint;
import android.util.Log;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtils {

    // fun to get user id
    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }


    // fun to check if the user is logged in or not
    public static boolean isLoggedIn() {
        return currentUserId() != null;
    }

    // fun to get details of the current user
    public static DocumentReference currentUserDetails () {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    // fun to refer to users collections
    public static CollectionReference allCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    // fun to get reference to a specific chat room
    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    // fun to refer to chats collections in a specific chat room
    public static CollectionReference getChatroomCollectionReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    // fun to make a chat room id
    public static String getChatroomId(String user1, String user2) {
        if (user1.hashCode()<user2.hashCode()) {
            return user1+"_"+user2;
        } else
            return user2+"_"+user1;
    }

    // fun to refer to chatrooms collections
    public static CollectionReference allChatroomCollections () {
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }

    // fun to get other user info
    public static DocumentReference otherChatroomReference (List<String> userIds) {
        if (userIds.get(0).equals(FirebaseUtils.currentUserId()))
            return allCollectionReference().document(userIds.get(1));
        else
            return allCollectionReference().document(userIds.get(0));
    }

    // date format function
    @SuppressLint("SimpleDateFormat")
    public static String timeFormat (Timestamp timestamp) {
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

    // logout function
    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    // fun to increment the unread count
    public static void incrementUnreadCount(String chatroomId, String userId) {
        String fieldPath = "unreadMessages." + userId;

        getChatroomReference(chatroomId)
                .update(fieldPath, FieldValue.increment(1))
                .addOnSuccessListener(aVoid -> Log.d("TAG", "Unread count incremented for user: " + userId))

                .addOnFailureListener(e -> Log.e("TAG", "Failed to increment unread count", e));
    }

    // fun to reset unread count to 0
    public static void resetUnreadCount(String chatroomId, String userId) {
        String fieldPath = "unreadMessages." + userId;

        getChatroomReference(chatroomId)
                .update(fieldPath, 0)
                .addOnSuccessListener(aVoid -> Log.d("TAG", "Unread count reset for user: " + userId))

                .addOnFailureListener(e -> Log.e("TAG", "Failed to reset unread count", e));
    }

    // fun to check if the other user is in the chat or not
    public static void setActiveStatus(String chatroomId,String userId, boolean isActive) {
        getChatroomReference(chatroomId)
                .update("isInActiveChat." + userId, isActive);
    }

}
