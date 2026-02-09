package com.example.chatapplication.ui.chat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.chatapplication.data.model.ChatMessageModel;
import com.example.chatapplication.utils.FirebaseUtils;
import com.example.chatapplication.databinding.ChatMessageRowBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecyclerViewAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerViewAdapter.ChatMessageViewHolder> {

    Context context;
    public ChatRecyclerViewAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;

    }


    @Override
    protected void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position, @NonNull ChatMessageModel model) {

        if (model.getSenderId().equals(FirebaseUtils.currentUserId())) {
            holder.binding.leftTv.setVisibility(View.GONE);
            holder.binding.rightTv.setVisibility(View.VISIBLE);
            holder.binding.rightTv.setText(model.getMessage());
        } else {
            holder.binding.rightTv.setVisibility(View.GONE);
            holder.binding.leftTv.setVisibility(View.VISIBLE);
            holder.binding.leftTv.setText(model.getMessage());
        }
       
    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChatMessageRowBinding binding = ChatMessageRowBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new ChatMessageViewHolder(binding);
    }

    public static class ChatMessageViewHolder extends RecyclerView.ViewHolder{
        ChatMessageRowBinding binding;

        public ChatMessageViewHolder(@NonNull ChatMessageRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
