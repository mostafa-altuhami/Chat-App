package com.example.chatapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapplication.ChatActivity;
import com.example.chatapplication.Model.UserModel;
import com.example.chatapplication.R;
import com.example.chatapplication.Utils.AndroidUtil;
import com.example.chatapplication.Utils.FirebaseUtils;
import com.example.chatapplication.databinding.SearchUserRecyclerRowBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.Objects;

// rv for search items
public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModelViewHolder> {

   Context context;
    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;

    }


    @Override
    protected void onBindViewHolder(@NonNull UserModelViewHolder holder, int position, @NonNull UserModel model) {
        holder.binding.searchUserRowTvUsernameId.setText(model.getUsername());
        holder.binding.searchUserRowTvPhoneId.setText(model.getPhone());
        // check if i search about me
        if(Objects.equals(model.getUserId(), FirebaseUtils.currentUserId())) {
            holder.binding.searchUserRowTvUsernameId.setText(String.format("%s (Me)", model.getUsername()));
        }
        Glide.with(context)
                .load(model.getImageUrl())
                .circleCrop()
                .placeholder(R.drawable.user)
                .into(holder.binding.searchUserRowIvPictureId.profileImageView);


        // onClick event
        holder.itemView.setOnClickListener(view -> {
           // navigate to chat activity
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public UserModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchUserRecyclerRowBinding binding = SearchUserRecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new UserModelViewHolder(binding);
    }

    public static class UserModelViewHolder extends RecyclerView.ViewHolder{
        SearchUserRecyclerRowBinding binding;

        public UserModelViewHolder(@NonNull SearchUserRecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
