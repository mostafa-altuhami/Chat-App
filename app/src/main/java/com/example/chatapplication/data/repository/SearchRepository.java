package com.example.chatapplication.data.repository;

import androidx.lifecycle.LifecycleOwner;

import com.example.chatapplication.data.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SearchRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirestoreRecyclerOptions<UserModel> getSearchOptions(LifecycleOwner owner, String searchTerm) {
        Query query = db.collection("users")
                .orderBy("username")
                .startAt(searchTerm)
                .endAt(searchTerm + "\uf8ff");


        return new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .setLifecycleOwner(owner)
                .build();


    }

}
