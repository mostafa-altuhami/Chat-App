//package com.example.chatapplication.data.repository;
//
//import androidx.lifecycle.LifecycleOwner;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import com.example.chatapplication.data.model.ChatMessageModel;
//import com.example.chatapplication.data.model.ChatroomModel;
//import com.example.chatapplication.utils.FirebaseUtils;
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
//import com.google.firebase.Timestamp;
//import com.google.firebase.firestore.FieldValue;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.ListenerRegistration;
//import com.google.firebase.firestore.Query;
//
//import java.util.List;
//import java.util.Objects;
//
//public class ChatMessageRepository {
//
//    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();
//    private ListenerRegistration messagesListener;
//
//    public Query getChatMessagesQuery(String chatroomId) {
//        return db.collection("chatrooms")
//                .document(chatroomId)
//                .collection("chats")
//                .orderBy("timestamp", Query.Direction.ASCENDING);
//    }
//
//    public FirestoreRecyclerOptions<ChatMessageModel> getChatMessagesOptions (LifecycleOwner owner, String chatroomId) {
//        return new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
//                .setQuery(getChatMessagesQuery(chatroomId), ChatMessageModel.class)
//                .setLifecycleOwner(owner)
//                .build();
//    }
//
//    public LiveData<String> getToastMessage() {
//        return toastMessage;
//    }
//
//    public LiveData<ChatroomModel> getOrCreateChatroomModel(String chatroomId, String otherUserId) {
//        MutableLiveData<ChatroomModel> chatroom = new MutableLiveData<>();
//
//        db.collection("chatrooms")
//                .document(chatroomId)
//                .get()
//                .addOnSuccessListener(doc -> {
//                    if (doc.exists())
//                        chatroom.setValue(doc.toObject(ChatroomModel.class));
//                    else {
//                        ChatroomModel newChatroom = new ChatroomModel(
//                                chatroomId,
//                                List.of(FirebaseUtils.currentUserId(), otherUserId),
//                                Timestamp.now(),
//                                ""
//                        );
//
//                        chatroom.setValue(newChatroom);
//
//                        db.collection("chatrooms")
//                                .document(chatroomId)
//                                .set(newChatroom)
//                                .addOnFailureListener( e -> {
//                                    chatroom.setValue(null);
//                                    toastMessage.setValue("Failed to load chat. Please try again.");
//                                });
//                    }
//
//                Objects.requireNonNull(chatroom.getValue()).setLastMessageTimestamp(Timestamp.now());
//                db.collection("chatrooms")
//                        .document(chatroomId)
//                        .update("lastMessageTimestamp", Timestamp.now())
//                        .addOnFailureListener(e -> {
//                            chatroom.setValue(null);
//                            toastMessage.setValue("Failed to update chatroom timestamp");
//                        });
//
//                });
//
//        return chatroom;
//    }
//
//    public LiveData<Boolean> incrementUnreadCount(String chatroomId, String userId) {
//        MutableLiveData<Boolean> success = new MutableLiveData<>();
//        String fieldPath = "unreadMessages." + userId;
//
//        db.collection("chatrooms")
//                .document(chatroomId)
//                .update(fieldPath, FieldValue.increment(1))
//                .addOnSuccessListener(aVoid -> success.setValue(true))
//                .addOnFailureListener(e -> {
//                    success.setValue(false);
//                });
//        return success;
//
//    }
//
//
//    public LiveData<Boolean> resetUnreadCount(String chatroomId, String userId) {
//        MutableLiveData<Boolean> success = new MutableLiveData<>();
//        String fieldPath = "unreadMessages." + userId;
//
//        db.collection("chatrooms")
//                .document(chatroomId)
//                .update(fieldPath, 0)
//                .addOnSuccessListener(aVoid -> success.setValue(true))
//                .addOnFailureListener(e -> success.setValue(false));
//
//        return success;
//    }
//
//    public void setActiveStatus(String chatroomId,String userId, boolean isActive) {
//        db.collection("chatrooms")
//                .document(chatroomId)
//                .update("isInActiveChat." + userId, isActive);
//    }
//
//    public LiveData<Boolean> sendMessage(String chatroomId, String message) {
//        MutableLiveData<Boolean> success = new MutableLiveData<>();
//
//        db.collection("chatrooms")
//                .document(chatroomId)
//                .update("lastMessage", message,
//                "lastMessageSenderId", FirebaseUtils.currentUserId(),
//                "lastMessageTimestamp", Timestamp.now())
//                .addOnFailureListener(e -> {
//                    success.setValue(false);
//                    toastMessage.setValue("Failed to send message. Please try again.");
//                });
//
//        ChatMessageModel messageModel = new ChatMessageModel(
//                message,
//                FirebaseUtils.currentUserId(),
//                Timestamp.now()
//        );
//
//        db.collection("chatrooms")
//                .document(chatroomId)
//                .collection("chats")
//                .add(messageModel)
//                .addOnCompleteListener( task -> {
//                    if (task.isSuccessful()) {
//                        success.setValue(true);
//                    } else {
//                        success.setValue(false);
//                        toastMessage.setValue("Failed to send message. Please try again.");
//                    }
//                });
//        return success;
//    }
//
//    public LiveData<ChatroomModel> getChatroomModel(String chatroomId) {
//        MutableLiveData<ChatroomModel> chatroom = new MutableLiveData<>();
//
//        db.collection("chatrooms")
//                .document(chatroomId)
//                .get()
//                .addOnSuccessListener(doc -> {
//                    if (doc.exists())
//                        chatroom.setValue(doc.toObject(ChatroomModel.class));
//                    else
//                        chatroom.setValue(null);
//                })
//                .addOnFailureListener(e ->
//                        chatroom.setValue(null)
//                );
//
//        return chatroom;
//    }
//
//    public void removeMessagesListener() {
//        if (messagesListener != null) {
//            messagesListener.remove();
//            messagesListener = null;
//        }
//    }
//
//}

package com.example.chatapplication.data.repository;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chatapplication.data.model.ChatMessageModel;
import com.example.chatapplication.data.model.ChatroomModel;
import com.example.chatapplication.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class ChatMessageRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public void getOrCreateChatroom(String chatroomId, String otherUserId) {
        db.collection("chatrooms")
                .document(chatroomId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        ChatroomModel room = new ChatroomModel(
                                chatroomId,
                                List.of(FirebaseUtils.currentUserId(), otherUserId),
                                Timestamp.now(),
                                ""
                        );
                        db.collection("chatrooms").document(chatroomId).set(room);
                    }
                })
                .addOnFailureListener(e ->
                        toastMessage.setValue("Failed to load chat")
                );
    }

    public void sendMessage(String chatroomId, String message) {
        ChatMessageModel model = new ChatMessageModel(
                message,
                FirebaseUtils.currentUserId(),
                Timestamp.now()
        );

        db.collection("chatrooms")
                .document(chatroomId)
                .update(
                        "lastMessage", message,
                        "lastMessageSenderId", FirebaseUtils.currentUserId(),
                        "lastMessageTimestamp", Timestamp.now()
                );

        db.collection("chatrooms")
                .document(chatroomId)
                .collection("chats")
                .add(model)
                .addOnFailureListener(e ->
                        toastMessage.setValue("Failed to send message")
                );
    }

    public void resetUnreadCount(String chatroomId) {
        db.collection("chatrooms")
                .document(chatroomId)
                .update(
                        "unreadMessages." + FirebaseUtils.currentUserId(),
                        0
                );
    }

    public void setActiveStatus(String chatroomId, boolean active) {
        db.collection("chatrooms")
                .document(chatroomId)
                .update(
                        "isInActiveChat." + FirebaseUtils.currentUserId(),
                        active
                );
    }

    public FirestoreRecyclerOptions<ChatMessageModel>
    getChatMessagesOptions(LifecycleOwner owner, String chatroomId) {

        Query query = db.collection("chatrooms")
                .document(chatroomId)
                .collection("chats")
                .orderBy("timestamp", Query.Direction.ASCENDING);

        return new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class)
                .setLifecycleOwner(owner)
                .build();
    }

    public void clear() {
        // reserved for future listeners
    }
}
