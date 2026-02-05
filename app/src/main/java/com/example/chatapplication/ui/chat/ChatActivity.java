package com.example.chatapplication.ui.chat;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.chatapplication.data.model.ChatMessageModel;
import com.example.chatapplication.data.model.ChatroomModel;
import com.example.chatapplication.data.model.UserModel;
import com.example.chatapplication.R;
import com.example.chatapplication.utils.AndroidUtil;
import com.example.chatapplication.utils.FirebaseUtils;
import com.example.chatapplication.ui.chat.adapter.ChatRecyclerViewAdapter;
import com.example.chatapplication.databinding.ActivityChatBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import java.util.Arrays;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private UserModel model;
    private ChatroomModel chatroomModel;
    private String chatroomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.chatToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);


        model = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtils.getChatroomId(FirebaseUtils.currentUserId(), model.getUserId());


        binding.chatUsername.setText(model.getUsername());

        Glide.with(getBaseContext())
                .load(model.getImageUrl())
                .circleCrop()
                .placeholder(R.drawable.ic_person)
                .into(binding.chatProfilePic.profileImageView);

        binding.chatToolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        getOrCreateChatroomModel();

        binding.chatIbSend.setOnClickListener(view -> {
            String message = binding.chatEtMessage.getText().toString().trim();
            if (message.isEmpty())
                return;

            FirebaseUtils.getChatroomReference(chatroomId)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        ChatroomModel room = snapshot.toObject(ChatroomModel.class);
                        if (room != null && !room.isUserInChat(model.getUserId())) {
                            FirebaseUtils.incrementUnreadCount(chatroomId, model.getUserId());
                        }
                        sendMessageToUser(message);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ChatActivity", "Failed to check chat status", e);
                        sendMessageToUser(message);

                    });

        });

        setupRecyclerView();

    }


    private void setupRecyclerView() {
        Query query = FirebaseUtils.getChatroomCollectionReference(chatroomId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class).build();

        ChatRecyclerViewAdapter adapter = new ChatRecyclerViewAdapter(options, ChatActivity.this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);
        binding.chatRvMessages.setLayoutManager(manager);
        binding.chatRvMessages.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                binding.chatRvMessages.smoothScrollToPosition(0);
            }
        });


    }

    private void getOrCreateChatroomModel() {
        FirebaseUtils.getChatroomReference(chatroomId).get().addOnCompleteListener(task -> {
           if (task.isSuccessful()) {
               chatroomModel = task.getResult().toObject(ChatroomModel.class);
               if (chatroomModel == null) {
                   chatroomModel = new ChatroomModel(
                           chatroomId,
                           Arrays.asList(FirebaseUtils.currentUserId(), model.getUserId()),
                           Timestamp.now(),
                           ""
                   );
                   FirebaseUtils.getChatroomReference(chatroomId).set(chatroomModel)
                           .addOnSuccessListener(aVoid -> Log.d("ChatActivity", "Chatroom created successfully"))
                           .addOnFailureListener(e -> {
                               AndroidUtil.showToast(this, "Failed to load chat. Please try again.");
                               finish(); // Close activity if can't create chatroom
                           });
               } else {
                   chatroomModel.setLastMessageTimestamp(Timestamp.now());
                   FirebaseUtils.getChatroomReference(chatroomId).update("lastMessageTimestamp", Timestamp.now())
                           .addOnSuccessListener(aVoid -> Log.d("ChatActivity", "Chatroom updated successfully"))
                           .addOnFailureListener(e -> Log.e("ChatActivity", "Failed to update chatroom timestamp", e));

               }
           } else {
               AndroidUtil.showToast(this, "Failed to load chat. Please check your connection.");
               finish();
           }
        });
    }

    // fun to send message to user
    private void sendMessageToUser(String message) {

        chatroomModel.setLastMessage(message);
        chatroomModel.setLastMessageSenderId(FirebaseUtils.currentUserId());
        chatroomModel.setLastMessageTimestamp(Timestamp.now());

        FirebaseUtils.getChatroomReference(chatroomId).update(
                "lastMessage", message,
                "lastMessageSenderId", FirebaseUtils.currentUserId(),
                "lastMessageTimestamp", Timestamp.now()
        ).addOnFailureListener(e -> {
            AndroidUtil.showToast(this, "Failed to send message. Please try again.");
        });

        ChatMessageModel messageModel = new ChatMessageModel(
                message,
                FirebaseUtils.currentUserId(),
                Timestamp.now()
        );

        FirebaseUtils.getChatroomCollectionReference(chatroomId)
                .add(messageModel).addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        binding.chatEtMessage.setText("");
                    } else {
                        AndroidUtil.showToast(this, "Failed to send message. Please try again.");
                    }

        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtils.setActiveStatus(chatroomId, FirebaseUtils.currentUserId(), true);
        FirebaseUtils.resetUnreadCount(chatroomId, FirebaseUtils.currentUserId());
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtils.setActiveStatus(chatroomId, FirebaseUtils.currentUserId(), false);
    }
}