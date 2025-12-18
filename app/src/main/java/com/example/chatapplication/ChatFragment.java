package com.example.chatapplication;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.chatapplication.Model.ChatroomModel;
import com.example.chatapplication.Utils.FirebaseUtils;
import com.example.chatapplication.adapters.ChatFragmentRvAdapter;
import com.example.chatapplication.databinding.FragmentChatBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

// chat fragment
public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private ChatFragmentRvAdapter adapter;


    public ChatFragment() {}


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false);
        setupRecyclerView();
        return binding.getRoot();
    }


    // fun to setup the recycle view
    private void setupRecyclerView() {
        // query to get all chats and sort them in descending order
        Query query = FirebaseUtils.allChatroomCollections()
                .whereArrayContains("userIds", FirebaseUtils.currentUserId())
                .orderBy("lastMessageTimestamp", Query.Direction.DESCENDING);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("ChatFragment", "Query returned " + task.getResult().size() + " chatrooms");
                task.getResult().getDocuments().forEach(doc -> Log.d("ChatFragment", "Chatroom: " + doc.getId() + ", Data: " + doc.getData()));
            } else {
                Log.e("ChatFragment", "Query failed: ", task.getException());
            }
        });

        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class)
                .setLifecycleOwner(requireActivity())
                .build();

        adapter = new ChatFragmentRvAdapter(options, requireContext());
        binding.chatFragmentRv.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.chatFragmentRv.setAdapter(adapter);
        // IT"s Important for rv normal behavior
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