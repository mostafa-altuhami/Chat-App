package com.example.chatapplication.ui.main.chats.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapplication.R;
import com.example.chatapplication.data.model.ChatroomModel;
import com.example.chatapplication.data.model.UserModel;
import com.example.chatapplication.databinding.ChatFragmentRvRowBinding;
import com.example.chatapplication.ui.chat.ChatActivity;
import com.example.chatapplication.utils.AndroidUtil;
import com.example.chatapplication.utils.FirebaseUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.ListenerRegistration;

public class ChatFragmentRvAdapter extends
        FirestoreRecyclerAdapter<ChatroomModel, ChatFragmentRvAdapter.ChatFragmentRvViewHolder> {

    private final Context context;

    public ChatFragmentRvAdapter(
            @NonNull FirestoreRecyclerOptions<ChatroomModel> options,
            Context context
    ) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(
            @NonNull ChatFragmentRvViewHolder holder,
            int position,
            @NonNull ChatroomModel model
    ) {

        // Listen for the other user data
        ListenerRegistration registration =
                FirebaseUtils.otherChatroomReference(model.getUserIds())
                        .addSnapshotListener((snapshot, error) -> {

                            if (error != null || snapshot == null || !snapshot.exists()) return;

                            UserModel otherUser = snapshot.toObject(UserModel.class);
                            if (otherUser == null) return;

                            bindUserData(holder, model, otherUser);
                        });

        holder.setListenerRegistration(registration);
    }

    private void bindUserData(
            @NonNull ChatFragmentRvViewHolder holder,
            @NonNull ChatroomModel chatroom,
            @NonNull UserModel otherUser
    ) {

        boolean lastMessageByMe =
                chatroom.getLastMessageSenderId()
                        .equals(FirebaseUtils.currentUserId());

        holder.binding.chatFragmentRvTvUsername.setText(otherUser.getUsername());
        holder.binding.chatFragmentRvTvTime.setText(
                FirebaseUtils.timeFormat(chatroom.getLastMessageTimestamp())
        );

        Glide.with(context)
                .load(otherUser.getImageUrl())
                .circleCrop()
                .placeholder(R.drawable.ic_person)
                .into(holder.binding.chatFragmentRvIvPhoto.profileImageView);

        if (lastMessageByMe) {
            holder.binding.chatFragmentRvTvDescription.setText(String.format("You: %s", chatroom.getLastMessage()));
        } else {
            holder.binding.chatFragmentRvTvDescription.setText(chatroom.getLastMessage());
        }

        int unreadCount =
                chatroom.getUnreadCountForUser(FirebaseUtils.currentUserId());

        if (unreadCount > 0) {
            holder.binding.chatFragmentRvTvUnreadCount.setVisibility(View.VISIBLE);
            holder.binding.chatFragmentRvTvUnreadCount.setText(
                    unreadCount > 99 ? "99+" : String.valueOf(unreadCount)
            );
        } else {
            holder.binding.chatFragmentRvTvUnreadCount.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, otherUser);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public ChatFragmentRvViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        ChatFragmentRvRowBinding binding =
                ChatFragmentRvRowBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );
        return new ChatFragmentRvViewHolder(binding);
    }

    @Override
    public void onViewRecycled(@NonNull ChatFragmentRvViewHolder holder) {
        super.onViewRecycled(holder);
        holder.clearListener();
    }

    public static class ChatFragmentRvViewHolder extends RecyclerView.ViewHolder {

        final ChatFragmentRvRowBinding binding;
        private ListenerRegistration listenerRegistration;

        ChatFragmentRvViewHolder(ChatFragmentRvRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setListenerRegistration(ListenerRegistration registration) {
            clearListener();
            this.listenerRegistration = registration;
        }

        void clearListener() {
            if (listenerRegistration != null) {
                listenerRegistration.remove();
                listenerRegistration = null;
            }
        }
    }
}
