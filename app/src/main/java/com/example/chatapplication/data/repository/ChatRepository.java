package com.example.chatapplication.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chatapplication.data.model.ChatMessageModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import java.util.List;

public class ChatRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration listenerRegistration;

    public LiveData<List<ChatMessageModel>> listenMessages (String chatroomId) {

        MutableLiveData<List<ChatMessageModel>> messages = new MutableLiveData<>();

        listenerRegistration = db.collection("chats")
                .document(chatroomId)
                .collection("messages")
                .orderBy("timestamp")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        messages.setValue(value.toObjects(ChatMessageModel.class));
                    }
                });

        return messages;
    }


    public void sendMessage (String chatroomId, ChatMessageModel message) {
        db.collection("chats")
                .document(chatroomId)
                .collection("messages")
                .add(message);
    }

    public void removeListener() {
        if (listenerRegistration != null)
            listenerRegistration.remove();
    }
}
