package com.example.chatapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.chatapplication.ChatActivity;
import com.example.chatapplication.Model.ChatroomModel;
import com.example.chatapplication.Model.UserModel;
import com.example.chatapplication.R;
import com.example.chatapplication.Utils.AndroidUtil;
import com.example.chatapplication.Utils.FirebaseUtils;
import com.example.chatapplication.databinding.ChatFragmentRvRowBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

// rv that display all chat rooms
public class ChatFragmentRvAdapter extends
        FirestoreRecyclerAdapter<ChatroomModel, ChatFragmentRvAdapter.ChatFragmentRvViewHolder> {

    Context context;
    public ChatFragmentRvAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context) {
        super(options);
        this.context = context;

    }


    @Override
    protected void onBindViewHolder(@NonNull ChatFragmentRvViewHolder holder, int position, @NonNull ChatroomModel model) {


        FirebaseUtils.otherChatroomReference(model.getUserIds())
                .addSnapshotListener((snapshot, error) -> {
                    // check if there is an error
                    if (error != null || snapshot == null || !snapshot.exists()) return;

                    UserModel otherUser = snapshot.toObject(UserModel.class);

                    if (otherUser != null) {
                        // check if i sent the last message
                        boolean lastMessageByMe = model.getLastMessageSenderId().equals(FirebaseUtils.currentUserId());

                        holder.binding.chatFragmentRvTvUsername.setText(otherUser.getUsername());
                        holder.binding.chatFragmentRvTvTime.setText(FirebaseUtils.timeFormat(model.getLastMessageTimestamp()));

                        Glide.with(context)
                                .load(otherUser.getImageUrl())
                                .circleCrop()
                                .placeholder(R.drawable.user)
                                .into(holder.binding.chatFragmentRvIvPhoto.profileImageView);

                        // if the last message was by me add "You" before it
                        if (lastMessageByMe) {
                            holder.binding.chatFragmentRvTvDescription.setText(String.format("You: %s", model.getLastMessage()));
                        } else {
                            holder.binding.chatFragmentRvTvDescription.setText(model.getLastMessage());
                        }

                        // get unread messages count
                        String currentUserId = FirebaseUtils.currentUserId();
                        int unreadCount = model.getUnreadCountForUser(currentUserId);

                        // handle states of unread count
                        if (unreadCount > 0) {
                            holder.binding.chatFragmentRvTvUnreadCount.setVisibility(View.VISIBLE);

                            if (unreadCount > 99) {
                                holder.binding.chatFragmentRvTvUnreadCount.setText(R.string._99);
                            } else {
                                holder.binding.chatFragmentRvTvUnreadCount.setText(String.valueOf(unreadCount));
                            }
                        } else {
                            holder.binding.chatFragmentRvTvUnreadCount.setVisibility(View.GONE);
                        }

                        // onClick event
                        holder.itemView.setOnClickListener(view -> {
                            // open chat activity with data of the user
                            Intent intent = new Intent(context, ChatActivity.class);
                            AndroidUtil.passUserModelAsIntent(intent, otherUser);
                            context.startActivity(intent);
                        });
                    }
                });


    }

    @NonNull
    @Override
    public ChatFragmentRvViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ChatFragmentRvRowBinding binding = ChatFragmentRvRowBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new ChatFragmentRvViewHolder(binding);
    }

    public static class ChatFragmentRvViewHolder extends RecyclerView.ViewHolder{
        ChatFragmentRvRowBinding binding;

        public ChatFragmentRvViewHolder(@NonNull ChatFragmentRvRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}