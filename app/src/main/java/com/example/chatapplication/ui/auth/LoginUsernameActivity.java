package com.example.chatapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.chatapplication.R;
import com.example.chatapplication.data.model.UserModel;
import com.example.chatapplication.databinding.ActivityLoginUsernameBinding;
import com.example.chatapplication.ui.main.MainActivity;
import com.example.chatapplication.ui.main.profile.ProfileViewModel;
import com.example.chatapplication.utils.AndroidUtil;
import com.example.chatapplication.utils.FirebaseUtils;
import com.google.firebase.Timestamp;

public class LoginUsernameActivity extends AppCompatActivity {

    private ActivityLoginUsernameBinding binding;
    private String phoneNumber;
    private ProfileViewModel viewModel;
    private UserModel profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginUsernameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        phoneNumber = getIntent().getStringExtra("phone");
        setInProgress(false);
        getInfo();

        binding.btnStart.setOnClickListener(view ->
                setUsername()
        );

    }

    private void getInfo() {
        viewModel.getUserDetails()
                .observe(this, userModel -> {
                    setInProgress(false);
                    if (userModel != null) {
                        profile = userModel;
                        binding.etUsername.setText(userModel.getUsername());
                        Glide.with(getBaseContext())
                                .load(userModel.getImageUrl())
                                .circleCrop()
                                .placeholder(R.drawable.ic_person)
                                .into(binding.ivPersonIcon);
                    }
                });
    }


    private void setUsername () {

        String username = binding.etUsername.getText().toString();
        // if user entered invalid username it will show an error
        if (username.isEmpty() || username.length() < 3) {
            binding.etUsername.setError("username length should be at least 3 chars");
            return;
        }
        setInProgress(true);

        if (profile != null) {
            profile.setUsername(username);
        } else {
            profile = new UserModel(phoneNumber, username, Timestamp.now(), FirebaseUtils.currentUserId());
        }

        viewModel.updateUserDetails(profile)
                .observe(this, success -> {
                    if (success) {
                        setInProgress(false);
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else
                        AndroidUtil.showToast(this, "Failed to save username. Please try again.");
                });
    }

    void setInProgress (boolean inProgress) {
        if (inProgress) {
            binding.usernamePb.setVisibility(View.VISIBLE);
            binding.btnStart.setVisibility(View.GONE);
        } else {
            binding.usernamePb.setVisibility(View.GONE);
            binding.btnStart.setVisibility(View.VISIBLE);
        }
    }


}

