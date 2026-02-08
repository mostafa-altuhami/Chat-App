package com.example.chatapplication.ui.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chatapplication.data.model.ChatMessageModel;
import com.example.chatapplication.data.repository.ChatRepository;

import java.util.List;

public class ChatMessageViewModel extends ViewModel {

    private final ChatRepository repository = new ChatRepository();

    private final MutableLiveData<List<ChatMessageModel>> messages =
            new MutableLiveData<>();

    private final MutableLiveData<String> error =
            new MutableLiveData<>();

    public LiveData<List<ChatMessageModel>> getMessages() {
        return messages;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void startListening(String chatroomId) {
        repository.listenToMessages(chatroomId, new ChatRepository.MessagesListener() {
            @Override
            public void onMessages(List<ChatMessageModel> data) {
                messages.setValue(data);
            }

            @Override
            public void onError(String err) {
                error.setValue(err);
            }
        });
    }

    public void sendMessage(String chatroomId, ChatMessageModel message) {
        repository.sendMessage(chatroomId, message);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.removeMessagesListener();
    }
}
