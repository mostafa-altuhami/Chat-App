package com.example.chatapplication.ui.search;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.chatapplication.ui.search.adapter.AddRecyclerAdapter;
import com.example.chatapplication.databinding.ActivitySearchBinding;


public class AddActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    AddViewModel viewModel;
    AddRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.searchToolbar);
        viewModel = new ViewModelProvider(this).get(AddViewModel.class);


        binding.searchToolbar.setNavigationOnClickListener(view -> finish());


       setupRecyclerView();

    }


    private void setupRecyclerView() {
        adapter = new AddRecyclerAdapter(viewModel.getAddOptions(this), AddActivity.this);
        binding.searchRvUsers.setLayoutManager(new LinearLayoutManager(this));
        binding.searchRvUsers.setAdapter(adapter);
    }


}