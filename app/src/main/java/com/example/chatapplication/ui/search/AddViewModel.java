package com.example.chatapplication.ui.search;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import com.example.chatapplication.data.model.UserModel;
import com.example.chatapplication.data.repository.AddRepository;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AddViewModel extends ViewModel {

    private final AddRepository repository = new AddRepository();

    public FirestoreRecyclerOptions<UserModel> getAddOptions(LifecycleOwner owner) {
        return repository.getAddOption(owner);
    }


}
