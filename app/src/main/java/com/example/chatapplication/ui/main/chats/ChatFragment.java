package com.example.chatapplication.ui.main.chats;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.chatapplication.ui.main.chats.adapter.ChatFragmentRvAdapter;
import com.example.chatapplication.databinding.FragmentChatBinding;


public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private ChatFragmentRvAdapter adapter;
    private ChatsViewModel viewModel;

    public ChatFragment() {}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ChatsViewModel.class);
        setupRecyclerView();

        return binding.getRoot();
    }


    private void setupRecyclerView() {
        adapter = new ChatFragmentRvAdapter(viewModel.getChatroomsOptions(getViewLifecycleOwner()), requireContext());
        binding.chatFragmentRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.chatFragmentRv.setAdapter(adapter);
        binding.chatFragmentRv.setItemAnimator(null);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter != null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null)
            adapter.stopListening();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (binding != null) {
            adapter.stopListening();
            binding.chatFragmentRv.setAdapter(null);
        }
        binding = null;
        adapter = null;
    }

}