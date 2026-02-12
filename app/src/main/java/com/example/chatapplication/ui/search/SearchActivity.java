package com.example.chatapplication.ui.search;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.chatapplication.data.model.UserModel;
import com.example.chatapplication.utils.FirebaseUtils;
import com.example.chatapplication.ui.search.adapter.SearchUserRecyclerAdapter;
import com.example.chatapplication.databinding.ActivitySearchBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    SearchViewModel viewModel;
    SearchUserRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.searchToolbar);
        binding.searchEdUsername.requestFocus();
        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);


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

        adapter = new SearchUserRecyclerAdapter(viewModel.getSearchOptions(this, searchTerm),SearchActivity.this);
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