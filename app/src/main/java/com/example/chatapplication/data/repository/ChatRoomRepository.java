package com.example.chatapplication.data.repository;

import static com.example.chatapplication.utils.FirebaseUtils.currentUserId;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import com.example.chatapplication.data.model.ChatMessageModel;
import com.example.chatapplication.data.model.ChatroomModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.List;

public class ChatRoomRepository {

    private final FirebaseFirestore db;
    private static ChatRoomRepository instance;


    public ChatRoomRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public static ChatRoomRepository getRepositoryInstance() {

        if (instance == null) {
            instance = new ChatRoomRepository();
        }

        return instance;
    }


    public Query getChatroomsQuery() {
        return db.collection("chatrooms")
                .whereArrayContains("userIds", currentUserId())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);
    }

    public FirestoreRecyclerOptions<ChatroomModel> getOptions (LifecycleOwner owner) {
        return new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(getChatroomsQuery(), ChatroomModel.class)
                .setLifecycleOwner(owner)
                .build();
    }

}
