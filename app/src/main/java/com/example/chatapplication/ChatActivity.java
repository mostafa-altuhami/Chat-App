package com.example.chatapplication;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.chatapplication.Model.ChatMessageModel;
import com.example.chatapplication.Model.ChatroomModel;
import com.example.chatapplication.Model.UserModel;
import com.example.chatapplication.Utils.AndroidUtil;
import com.example.chatapplication.Utils.FCMTokenUtil;
import com.example.chatapplication.Utils.FirebaseUtils;
import com.example.chatapplication.adapters.ChatRecyclerViewAdapter;
import com.example.chatapplication.databinding.ActivityChatBinding;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import java.util.Arrays;
import java.util.Objects;

// activity for chats
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


        // get other UserModel
        model = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtils.getChatroomId(FirebaseUtils.currentUserId(), model.getUserId());


        binding.chatUsername.setText(model.getUsername());

        Glide.with(getBaseContext())
                .load(model.getImageUrl())
                .circleCrop()
                .placeholder(R.drawable.user)
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
                        // Still send message even if check fails
                        sendMessageToUser(message);

                    });

        });

        setupRecyclerView();

    }


    // fun to setup recycle view
    private void setupRecyclerView() {
        // query for descending messages
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
        // observe message to be updated with the user ui
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                // go to the last message
                binding.chatRvMessages.smoothScrollToPosition(0);
            }
        });


    }

    // fun to get chat room model
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
                               Log.e("ChatActivity", "Failed to create chatroom", e);
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
               Log.e("ChatActivity", "Failed to get chatroom");
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
            Log.e("ChatActivity", "Failed to update chatroom", e);
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
                        sendNotificationToRecipient(message);
                    } else {
                        Log.e("ChatActivity", "Failed to send message");
                        AndroidUtil.showToast(this, "Failed to send message. Please try again.");
                    }

        });
    }

    // fun to send notifications to user
    private void sendNotificationToRecipient(String message) {
        Log.d("ChatActivity", "Attempting to send notification for message: " + message);

        FirebaseUtils.allCollectionReference()
                .document(model.getUserId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        UserModel recipient = task.getResult().toObject(UserModel.class);

                        if (recipient == null) {
                            Log.e("ChatActivity", "Recipient user model is null");
                            return;
                        }

                        Log.d("ChatActivity", "Recipient FCM token: " + recipient.getFcmToken());

                        if (recipient.getFcmToken() != null && !recipient.getFcmToken().isEmpty()) {
                            Log.d("ChatActivity", "Valid FCM token found, getting sender info...");

                            FirebaseUtils.currentUserDetails().get().addOnCompleteListener(senderTask -> {
                                if (senderTask.isSuccessful() && senderTask.getResult() != null) {
                                    UserModel sender = senderTask.getResult().toObject(UserModel.class);
                                    if (sender != null) {
                                        String title = sender.getUsername();

                                        Log.d("ChatActivity", "Sending notification - Title: " + title + ", Body: " + message);
                                        Log.d("ChatActivity", "Recipient token: " + recipient.getFcmToken());

                                        FCMTokenUtil.sendNotification(
                                                ChatActivity.this,
                                                recipient.getFcmToken(),
                                                title,
                                                message,
                                                FirebaseUtils.currentUserId(),
                                                sender.getUsername()
                                        );
                                    } else {
                                        Log.e("ChatActivity", "Sender user model is null");
                                    }
                                } else {
                                    Log.e("ChatActivity", "Failed to get sender info: " + senderTask.getException());
                                }
                            });
                        } else {
                            Log.e("ChatActivity", "Recipient FCM token is null or empty");
                        }
                    } else {
                        Log.e("ChatActivity", "Failed to get recipient info: " + task.getException());
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