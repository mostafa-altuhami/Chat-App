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

public class ChatRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration messagesListener;

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

    public void listenToMessages(
            String chatroomId,
            MessagesListener listener
    ) {
        removeMessagesListener();

        messagesListener = db.collection("chats")
                .document(chatroomId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        listener.onError(error.getMessage());
                        return;
                    }

                    if (snapshots != null) {
                        listener.onMessages(
                                snapshots.toObjects(ChatMessageModel.class)
                        );
                    }
                });
    }

    public void sendMessage(String chatroomId, ChatMessageModel message) {
        db.collection("chats")
                .document(chatroomId)
                .collection("messages")
                .add(message);
    }

    public void removeMessagesListener() {
        if (messagesListener != null) {
            messagesListener.remove();
        }
    }

    public interface MessagesListener {
        void onMessages(java.util.List<ChatMessageModel> messages);
        void onError(@Nullable String error);
    }
}
