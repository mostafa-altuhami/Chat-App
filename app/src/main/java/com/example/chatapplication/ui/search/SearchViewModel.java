package com.example.chatapplication.ui.search;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModel;

import com.example.chatapplication.data.model.UserModel;
import com.example.chatapplication.data.repository.SearchRepository;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchViewModel extends ViewModel {

    private final SearchRepository repository = new SearchRepository();

    public FirestoreRecyclerOptions<UserModel> getSearchOptions(LifecycleOwner owner, String searchTerm) {
        return repository.getSearchOptions(owner, searchTerm);
    }


}
