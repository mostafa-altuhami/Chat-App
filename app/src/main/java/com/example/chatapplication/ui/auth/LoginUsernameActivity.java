package com.example.chatapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.chatapplication.ui.main.MainActivity;
import com.example.chatapplication.data.model.UserModel;
import com.example.chatapplication.utils.AndroidUtil;
import com.example.chatapplication.utils.FirebaseUtils;
import com.example.chatapplication.databinding.ActivityLoginUsernameBinding;
import com.google.firebase.Timestamp;

public class LoginUsernameActivity extends AppCompatActivity {
    private ActivityLoginUsernameBinding binding;
    String phoneNumber;

    UserModel userModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginUsernameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        phoneNumber = getIntent().getExtras().getString("phone");
        getInfo();

        binding.usernameBtnIn.setOnClickListener(view -> setUsername());

    }

    // fun to set the username
    private void setUsername () {

        String username = binding.usernameEtUsername.getText().toString();
        // if user entered invalid username it will show an error
        if (username.isEmpty() || username.length() < 3) {
            binding.usernameEtUsername.setError("username length should be at least 3 chars");
            return;
        }
        setInProgress(true);

        if (userModel!= null) {
            userModel.setUsername(username);
        } else {
            userModel = new UserModel(phoneNumber, username, Timestamp.now(), FirebaseUtils.currentUserId());
        }

        FirebaseUtils.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                AndroidUtil.showToast(this, "Failed to save username. Please try again.");
            }
        });
    }
    private void getInfo() {
        setInProgress(true);
        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(task -> {
            setInProgress(false);
            if (task.isSuccessful()) {
                // get info
                 userModel = task.getResult().toObject(UserModel.class);
                if (userModel != null) {
                    binding.usernameEtUsername.setText(userModel.getUsername());
                    Glide.with(getBaseContext())
                            .load(userModel.getImageUrl())
                            .circleCrop()
                            .into(binding.usernameIvPicture);
                }
            } else {
                AndroidUtil.showToast(this , "Unknown error occurred");
            }
        });
    }



    void setInProgress (boolean inProgress) {
        if (inProgress) {
            binding.usernamePb.setVisibility(View.VISIBLE);
            binding.usernameBtnIn.setVisibility(View.GONE);
        } else {
            binding.usernamePb.setVisibility(View.GONE);
            binding.usernameBtnIn.setVisibility(View.VISIBLE);
        }
    }
}