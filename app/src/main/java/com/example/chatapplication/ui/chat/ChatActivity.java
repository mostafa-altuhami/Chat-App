package com.example.chatapplication.ui.chat;


import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.chatapplication.data.model.UserModel;
import com.example.chatapplication.R;
import com.example.chatapplication.utils.AndroidUtil;
import com.example.chatapplication.utils.FirebaseUtils;
import com.example.chatapplication.ui.chat.adapter.ChatRecyclerViewAdapter;
import com.example.chatapplication.databinding.ActivityChatBinding;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private UserModel model;
    private String chatroomId;
    private ChatMessageViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.chatToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        viewModel = new ViewModelProvider(this).get(ChatMessageViewModel.class);

        model = AndroidUtil.getUserModelFromIntent(getIntent());
        chatroomId = FirebaseUtils.getChatroomId(FirebaseUtils.currentUserId(), model.getUserId());

        setupToolbar();
        setupRecyclerView();
        observeViewModel();

        viewModel.initChatroom(chatroomId, model.getUserId());
        binding.chatIbSend.setOnClickListener(view -> {
            String message = binding.chatEtMessage.getText().toString().trim();
            if (message.isEmpty())
                return;

            viewModel.sendMessage(chatroomId, message, model.getUserId());

            binding.chatEtMessage.setText("");

        });



    }

    private void setupToolbar() {
        binding.chatUsername.setText(model.getUsername());

        Glide.with(this)
                .load(model.getImageUrl())
                .circleCrop()
                .placeholder(R.drawable.ic_person)
                .into(binding.chatProfilePic.profileImageView);

        binding.chatToolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    private void setupRecyclerView() {

        ChatRecyclerViewAdapter adapter = new ChatRecyclerViewAdapter(viewModel.getChatMessagesOptions(this,
                chatroomId), ChatActivity.this);
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
        viewModel.setActiveStatus(chatroomId, FirebaseUtils.currentUserId(), true);
        viewModel.resetUnreadCount(chatroomId, FirebaseUtils.currentUserId());
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.setActiveStatus(chatroomId, FirebaseUtils.currentUserId(), false);
    }
}

