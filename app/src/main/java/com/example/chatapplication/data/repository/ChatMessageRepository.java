package com.example.chatapplication.data.repository;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.chatapplication.data.model.ChatMessageModel;
import com.example.chatapplication.data.model.ChatroomModel;
import com.example.chatapplication.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatMessageRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();

    public Query getChatMessagesQuery(String chatroomId) {
        return db.collection("chatrooms")
                .document(chatroomId)
                .collection("chats")
                .orderBy("timestamp", Query.Direction.ASCENDING);
    }

    public FirestoreRecyclerOptions<ChatMessageModel> getChatMessagesOptions(LifecycleOwner owner, String chatroomId) {
        return new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(getChatMessagesQuery(chatroomId), ChatMessageModel.class)
                .setLifecycleOwner(owner)
                .build();
    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public void getOrCreateChatroomModel(String chatroomId, String otherUserId) {

        db.collection("chatrooms")
                .document(chatroomId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {

                        Map<String, Object> unreadMap = new HashMap<>();
                        unreadMap.put(FirebaseUtils.currentUserId(), 0);
                        unreadMap.put(otherUserId, 0);

                        Map<String, Object> activeMap = new HashMap<>();
                        activeMap.put(FirebaseUtils.currentUserId(), false);
                        activeMap.put(otherUserId, false);

                        ChatroomModel room = new ChatroomModel(
                                chatroomId,
                                List.of(FirebaseUtils.currentUserId(), otherUserId),
                                Timestamp.now(),
                                ""
                        );

                        db.collection("chatrooms")
                                .document(chatroomId)
                                .set(room);

                        db.collection("chatrooms")
                                .document(chatroomId)
                                .update("unreadMessages", unreadMap,
                                        "isInActiveChat", activeMap);
                    }
                })
                .addOnFailureListener(e ->
                        toastMessage.setValue("Failed to create chatroom")
                );
    }


    public void resetUnreadCount(String chatroomId, String userId) {
        String fieldPath = "unreadMessages." + userId;

        db.collection("chatrooms")
                .document(chatroomId)
                .update(fieldPath, 0);


    }

    public void setActiveStatus(String chatroomId, String userId, boolean isActive) {
        db.collection("chatrooms")
                .document(chatroomId)
                .update("isInActiveChat." + userId, isActive);
    }

    public void sendMessage(String chatroomId, String message, String otherUserId) {

        String currentUserId = FirebaseUtils.currentUserId();
        Timestamp now = Timestamp.now();

        db.runTransaction(transaction -> {

            DocumentReference chatroomRef = db.collection("chatrooms").document(chatroomId);
            DocumentSnapshot chatSnapshot = transaction.get(chatroomRef);

            ChatroomModel room = chatSnapshot.toObject(ChatroomModel.class);
            if (room == null) {
                throw new RuntimeException("Chatroom not found");
            }

            Boolean isOtherActive =
                    room.getIsInActiveChat().get(otherUserId);

            Map<String, Object> updates = new HashMap<>();

            updates.put("lastMessage", message);
            updates.put("lastMessageSenderId", currentUserId);
            updates.put("lastMessageTimestamp", now);

            if (isOtherActive == null || !isOtherActive) {
                String unreadPath = "unreadMessages." + otherUserId;
                transaction.update(chatroomRef, unreadPath, FieldValue.increment(1));
            }

            transaction.update(chatroomRef, updates);

            DocumentReference chatRef = chatroomRef
                    .collection("chats")
                    .document();

            ChatMessageModel chatMessage = new ChatMessageModel(
                    message,
                    currentUserId,
                    now
            );

            transaction.set(chatRef, chatMessage);

            return null;

        }).addOnFailureListener(e ->
                toastMessage.setValue("Failed to send message")
        );
    }
}