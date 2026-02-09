//package com.example.chatapplication.ui.chat;
//
//import static androidx.lifecycle.LiveDataKt.observe;
//
//import android.os.Bundle;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.lifecycle.ViewModelProvider;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import com.bumptech.glide.Glide;
//import com.example.chatapplication.data.model.ChatroomModel;
//import com.example.chatapplication.data.model.UserModel;
//import com.example.chatapplication.R;
//import com.example.chatapplication.utils.AndroidUtil;
//import com.example.chatapplication.utils.FirebaseUtils;
//import com.example.chatapplication.ui.chat.adapter.ChatRecyclerViewAdapter;
//import com.example.chatapplication.databinding.ActivityChatBinding;
//import com.google.firebase.Timestamp;
//import java.util.Objects;
//
//public class ChatActivity extends AppCompatActivity {
//
//    private ActivityChatBinding binding;
//    private UserModel model;
//    private ChatroomModel chatroomModel;
//    private String chatroomId;
//    private ChatMessageViewModel viewModel;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityChatBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        setSupportActionBar(binding.chatToolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
//        viewModel = new ViewModelProvider(this).get(ChatMessageViewModel.class);
//
//        model = AndroidUtil.getUserModelFromIntent(getIntent());
//        chatroomId = FirebaseUtils.getChatroomId(FirebaseUtils.currentUserId(), model.getUserId());
//
//
//        binding.chatUsername.setText(model.getUsername());
//
//        Glide.with(getBaseContext())
//                .load(model.getImageUrl())
//                .circleCrop()
//                .placeholder(R.drawable.ic_person)
//                .into(binding.chatProfilePic.profileImageView);
//
//        binding.chatToolbar.setNavigationOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());
//
//        getOrCreateChatroomModel();
//
//        binding.chatIbSend.setOnClickListener(view -> {
//            String message = binding.chatEtMessage.getText().toString().trim();
//            if (message.isEmpty())
//                return;
//
//            ChatroomModel room = viewModel.getChatroomModel(chatroomId).getValue();
//                    if (room != null) {
//                        if (!room.isUserInChat(model.getUserId())) {
//                            viewModel.incrementUnreadCount(chatroomId, model.getUserId());
//                        }
//                        sendMessageToUser(message);
//                    }
//                    else {
//                        sendMessageToUser(message);
//
//                    }
//
//        });
//
//        setupRecyclerView();
//
//    }
//
//
//    private void setupRecyclerView() {
//
//        ChatRecyclerViewAdapter adapter = new ChatRecyclerViewAdapter(viewModel.getChatMessagesOptions(this,
//                chatroomId), ChatActivity.this);
//        LinearLayoutManager manager = new LinearLayoutManager(this);
//        manager.setReverseLayout(true);
//        manager.setStackFromEnd(true);
//        binding.chatRvMessages.setLayoutManager(manager);
//        binding.chatRvMessages.setAdapter(adapter);
//        adapter.startListening();
//        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//
//                binding.chatRvMessages.smoothScrollToPosition(0);
//            }
//        });
//
//
//    }
//
//    private void getOrCreateChatroomModel() {
//        viewModel.getOrCreateChatroomModel(chatroomId, model.getUserId())
//        .observe(this, room -> {
//            if (room == null) {
//                AndroidUtil.showToast(this, viewModel.getToastMessage().getValue());
//                return;
//            }
//            chatroomModel = room;
//        });
//
//    }
//
//    private void sendMessageToUser(String message) {
//
//        chatroomModel.setLastMessage(message);
//        chatroomModel.setLastMessageSenderId(FirebaseUtils.currentUserId());
//        chatroomModel.setLastMessageTimestamp(Timestamp.now());
//
//        boolean success = Boolean.TRUE.equals(viewModel.sendMessage(chatroomId, message).getValue());
//
//        if (!success) {
//            AndroidUtil.showToast(this, viewModel.getToastMessage().getValue());
//        } else {
//            binding.chatEtMessage.setText("");
//        }
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        viewModel.setActiveStatus(chatroomId, FirebaseUtils.currentUserId(), true);
//        viewModel.resetUnreadCount(chatroomId, FirebaseUtils.currentUserId());
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        viewModel.setActiveStatus(chatroomId, FirebaseUtils.currentUserId(), false);
//    }
//}

package com.example.chatapplication.ui.chat;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatapplication.R;
import com.example.chatapplication.data.model.UserModel;
import com.example.chatapplication.databinding.ActivityChatBinding;
import com.example.chatapplication.ui.chat.adapter.ChatRecyclerViewAdapter;
import com.example.chatapplication.utils.AndroidUtil;
import com.example.chatapplication.utils.FirebaseUtils;

import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private ChatMessageViewModel viewModel;
    private String chatroomId;
    private UserModel otherUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.chatToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        viewModel = new ViewModelProvider(this).get(ChatMessageViewModel.class);

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtils.getChatroomId(
                FirebaseUtils.currentUserId(),
                otherUser.getUserId()
        );

        setupToolbar();
        setupRecyclerView();
        observeViewModel();

        viewModel.initChatroom(chatroomId, otherUser.getUserId());

        binding.chatIbSend.setOnClickListener(v -> {
            String msg = binding.chatEtMessage.getText().toString().trim();
            if (!msg.isEmpty()) {
                viewModel.sendMessage(chatroomId, msg);
                binding.chatEtMessage.setText("");
            }
        });
    }

    private void setupToolbar() {
        binding.chatUsername.setText(otherUser.getUsername());

        Glide.with(this)
                .load(otherUser.getImageUrl())
                .circleCrop()
                .placeholder(R.drawable.ic_person)
                .into(binding.chatProfilePic.profileImageView);

        binding.chatToolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    private void setupRecyclerView() {
        ChatRecyclerViewAdapter adapter =
                new ChatRecyclerViewAdapter(
                        viewModel.getChatMessagesOptions(this, chatroomId),
                        this
                );

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        manager.setStackFromEnd(true);

        binding.chatRvMessages.setLayoutManager(manager);
        binding.chatRvMessages.setAdapter(adapter);

        adapter.startListening();

        adapter.registerAdapterDataObserver(
                new RecyclerView.AdapterDataObserver() {
                    @Override
                    public void onItemRangeInserted(int positionStart, int itemCount) {
                        binding.chatRvMessages.smoothScrollToPosition(0);
                    }
                }
        );

    }

    private void observeViewModel() {
        viewModel.getToastMessage().observe(this, msg -> {
            if (msg != null) {
                AndroidUtil.showToast(this, msg);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.setActiveStatus(chatroomId, true);
        viewModel.resetUnreadCount(chatroomId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.setActiveStatus(chatroomId, false);
    }
}
