package com.example.chatapplication.utils;

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

    public static String currentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }//


    public static boolean isLoggedIn() {
        return currentUserId() != null;
    }

    public static DocumentReference currentUserDetails () {//
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static CollectionReference allCollectionReference() {
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }

    public static CollectionReference getChatroomCollectionReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String user1, String user2) {
        if (user1.hashCode()<user2.hashCode()) {
            return user1+"_"+user2;
        } else
            return user2+"_"+user1;
    }


    public static DocumentReference otherChatroomReference (List<String> userIds) {
        if (userIds.get(0).equals(currentUserId()))
            return allCollectionReference().document(userIds.get(1));
        else
            return allCollectionReference().document(userIds.get(0));
    }


    @SuppressLint("SimpleDateFormat")
    public static String timeFormat (Timestamp timestamp) {
        return new SimpleDateFormat("HH:mm").format(timestamp.toDate());
    }

    public static void logout() {
        FirebaseAuth.getInstance().signOut();
    }//



}
