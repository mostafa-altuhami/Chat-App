package com.example.chatapplication.data.repository;

import static com.example.chatapplication.utils.Constants.CHATROOMS_COLLECTION_NAME;
import static com.example.chatapplication.utils.Constants.LAST_MESSAGE_TIMESTAMP_FIELD_NAME;
import static com.example.chatapplication.utils.Constants.USER_IDS_FIELD_NAME;
import static com.example.chatapplication.utils.FirebaseUtils.currentUserId;

import androidx.lifecycle.LifecycleOwner;
import com.example.chatapplication.data.model.ChatroomModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


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
        return db.collection(CHATROOMS_COLLECTION_NAME)
                .whereArrayContains(USER_IDS_FIELD_NAME, currentUserId())
                .orderBy(LAST_MESSAGE_TIMESTAMP_FIELD_NAME, Query.Direction.DESCENDING);
    }

    public FirestoreRecyclerOptions<ChatroomModel> getOptions (LifecycleOwner owner) {
        return new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(getChatroomsQuery(), ChatroomModel.class)
                .setLifecycleOwner(owner)
                .build();
    }

}
