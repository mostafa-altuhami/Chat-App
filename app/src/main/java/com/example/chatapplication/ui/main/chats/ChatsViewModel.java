package com.example.chatapplication.ui.main.chats;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import com.example.chatapplication.data.model.ChatroomModel;
import com.example.chatapplication.data.repository.ChatRepository;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatsViewModel extends ViewModel {

    private final ChatRepository repository = new ChatRepository();

    public FirestoreRecyclerOptions<ChatroomModel> getChatroomsOptions(LifecycleOwner owner) {
        return repository.getOptions(owner);
    }
}
