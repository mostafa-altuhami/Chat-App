package com.example.chatapplication.ui.search;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatapplication.data.model.UserModel;
import com.example.chatapplication.utils.FirebaseUtils;
import com.example.chatapplication.ui.search.adapter.SearchUserRecyclerAdapter;
import com.example.chatapplication.databinding.ActivitySearchBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    SearchUserRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.searchToolbar);
        binding.searchEdUsername.requestFocus();


        binding.searchToolbar.setNavigationOnClickListener(view -> finish());


        binding.searchIbSearch.setOnClickListener(view -> {
            String searchTerm = binding.searchEdUsername.getText().toString();
            if (searchTerm.isEmpty()) {
                binding.searchEdUsername.setError("Invalid Username");
                return;
            }

            setupSearchRecyclerView(searchTerm);
        });

    }


    private void setupSearchRecyclerView(String searchTerm) {
        Query query = FirebaseUtils.allCollectionReference()
                .orderBy("username")
                .startAt(searchTerm)
                .endAt(searchTerm + "\uf8ff");

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class).build();

        adapter = new SearchUserRecyclerAdapter(options,SearchActivity.this);
        binding.searchRvUsers.setLayoutManager(new LinearLayoutManager(this));
        binding.searchRvUsers.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null)
            adapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (adapter != null)
            adapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null)
            adapter.startListening();
    }


}