package com.example.chatapplication.ui.main.chats;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import com.example.chatapplication.data.model.ChatroomModel;
import com.example.chatapplication.data.repository.ChatRoomRepository;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatsViewModel extends ViewModel {

    private final ChatRoomRepository repository;

    public ChatsViewModel() {
        repository = ChatRoomRepository.getRepositoryInstance();
    }


    public FirestoreRecyclerOptions<ChatroomModel> getChatroomsOptions(LifecycleOwner owner) {
        return repository.getOptions(owner);
    }
}
