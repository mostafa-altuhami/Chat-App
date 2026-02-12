package com.example.chatapplication.ui.chat;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.chatapplication.data.model.ChatMessageModel;
import com.example.chatapplication.data.repository.ChatMessageRepository;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatMessageViewModel extends ViewModel {

    private final ChatMessageRepository repository = new ChatMessageRepository();


    public void sendMessage(String chatroomId, String message, String otherUserId) {
         repository.sendMessage(chatroomId, message, otherUserId);
    }

    public void initChatroom(String chatroomId, String otherUserId) {
        repository.getOrCreateChatroomModel(chatroomId, otherUserId);
    }
    public void setActiveStatus(String chatroomId,String userId, boolean isActive) {
        repository.setActiveStatus(chatroomId, userId, isActive);
    }

    public void resetUnreadCount(String chatroomId, String userId) {
         repository.resetUnreadCount(chatroomId, userId);
    }


    public LiveData<String> getToastMessage() {
        return repository.getToastMessage();
    }

    public FirestoreRecyclerOptions<ChatMessageModel> getChatMessagesOptions (LifecycleOwner owner, String chatroomId) {
        return repository.getChatMessagesOptions(owner, chatroomId);
    }

}
