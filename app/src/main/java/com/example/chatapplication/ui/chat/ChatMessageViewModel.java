//package com.example.chatapplication.ui.chat;
//
//import androidx.lifecycle.LifecycleOwner;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.ViewModel;
//import com.example.chatapplication.data.model.ChatMessageModel;
//import com.example.chatapplication.data.model.ChatroomModel;
//import com.example.chatapplication.data.repository.ChatMessageRepository;
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
//
//import java.util.List;
//
//public class ChatMessageViewModel extends ViewModel {
//
//    private final ChatMessageRepository repository = new ChatMessageRepository();
//
//
//    public LiveData<Boolean> sendMessage(String chatroomId, String message) {
//        return repository.sendMessage(chatroomId, message);
//    }
//
//    public void setActiveStatus(String chatroomId,String userId, boolean isActive) {
//        repository.setActiveStatus(chatroomId, userId, isActive);
//    }
//    public LiveData<Boolean> incrementUnreadCount(String chatroomId, String userId) {
//        return repository.incrementUnreadCount(chatroomId, userId);
//    }
//
//    public LiveData<Boolean> resetUnreadCount(String chatroomId, String userId) {
//        return repository.resetUnreadCount(chatroomId, userId);
//    }
//
//    public LiveData<ChatroomModel> getChatroomModel(String chatroomId) {
//        return repository.getChatroomModel(chatroomId);
//    }
//    public LiveData<ChatroomModel> getOrCreateChatroomModel(String chatroomId, String otherUserId) {
//        return repository.getOrCreateChatroomModel(chatroomId, otherUserId);
//    }
//
//    public LiveData<String> getToastMessage() {
//        return repository.getToastMessage();
//    }
//
//    public FirestoreRecyclerOptions<ChatMessageModel> getChatMessagesOptions (LifecycleOwner owner, String chatroomId) {
//        return repository.getChatMessagesOptions(owner, chatroomId);
//    }
//
//    @Override
//    protected void onCleared() {
//        super.onCleared();
//        repository.removeMessagesListener();
//    }
//}

package com.example.chatapplication.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.chatapplication.data.model.ChatMessageModel;
import com.example.chatapplication.data.repository.ChatMessageRepository;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatMessageViewModel extends ViewModel {

    private final ChatMessageRepository repository;

    public ChatMessageViewModel() {
        repository = new ChatMessageRepository();
    }

    public void initChatroom(String chatroomId, String otherUserId) {
        repository.getOrCreateChatroom(chatroomId, otherUserId);
    }

    public void sendMessage(String chatroomId, String message) {
        repository.sendMessage(chatroomId, message);
    }

    public void resetUnreadCount(String chatroomId) {
        repository.resetUnreadCount(chatroomId);
    }

    public void setActiveStatus(String chatroomId, boolean isActive) {
        repository.setActiveStatus(chatroomId, isActive);
    }

    public LiveData<String> getToastMessage() {
        return repository.getToastMessage();
    }

    public FirestoreRecyclerOptions<ChatMessageModel>
    getChatMessagesOptions(androidx.lifecycle.LifecycleOwner owner, String chatroomId) {
        return repository.getChatMessagesOptions(owner, chatroomId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.clear();
    }
}
