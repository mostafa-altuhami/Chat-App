package com.example.chatapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatapplication.Model.UserModel;
import com.example.chatapplication.Utils.FirebaseUtils;
import com.example.chatapplication.adapters.SearchUserRecyclerAdapter;
import com.example.chatapplication.databinding.ActivitySearchBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

// activity for searching the users
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


        //getOnBackPressedDispatcher().onBackPressed()
        binding.searchToolbar.setNavigationOnClickListener(view -> finish());


        binding.searchIbSearch.setOnClickListener(view -> {
            // get the user search text
            String searchTerm = binding.searchEdUsername.getText().toString();
            // check if it's empty
            if (searchTerm.isEmpty()) {
                binding.searchEdUsername.setError("Invalid Username");
                return;
            }

            // setup rv
            setupSearchRecyclerView(searchTerm);
        });

    }


    // fun to setup the recycle view
    private void setupSearchRecyclerView(String searchTerm) {
        // query to get the user by his nam's characters
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
            // binding.searchRvUsers.post(() -> adapter.notifyDataSetChanged());
    }


}