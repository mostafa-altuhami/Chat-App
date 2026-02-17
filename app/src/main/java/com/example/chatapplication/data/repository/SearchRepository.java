package com.example.chatapplication.data.repository;

import static com.example.chatapplication.utils.Constants.USERNAME_FIELD_NAME;
import static com.example.chatapplication.utils.Constants.USER_COLLECTION_NAME;

import androidx.lifecycle.LifecycleOwner;

import com.example.chatapplication.data.model.UserModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SearchRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public FirestoreRecyclerOptions<UserModel> getSearchOptions(LifecycleOwner owner, String searchTerm) {
        Query query = db.collection(USER_COLLECTION_NAME)
                .orderBy(USERNAME_FIELD_NAME)
                .startAt(searchTerm)
                .endAt(searchTerm + "\uf8ff");


        return new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .setLifecycleOwner(owner)
                .build();


    }

}
