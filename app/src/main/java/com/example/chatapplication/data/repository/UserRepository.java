package com.example.chatapplication.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chatapplication.data.model.UserModel;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<UserModel> getUserDetails(String userId) {
        MutableLiveData<UserModel> user = new MutableLiveData<>();

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener( doc -> {
                    if (doc.exists()) {
                        user.setValue(doc.toObject(UserModel.class));
                    }
                });

        return user;
    }

    public void addUser (UserModel userModel) {
        db.collection("users")
                .document(userModel.getUserId())
                .set(userModel);
    }
}
