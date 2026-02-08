package com.example.chatapplication.ui.main;

import static android.content.ContentValues.TAG;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapplication.ui.main.profile.ProfileFragment;
import com.example.chatapplication.R;
import com.example.chatapplication.ui.search.SearchActivity;
import com.example.chatapplication.utils.FirebaseUtils;
import com.example.chatapplication.databinding.ActivityMainBinding;
import com.example.chatapplication.ui.main.chats.ChatFragment;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ChatFragment chatFragment;
    private ProfileFragment profileFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.mainToolbar);

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();



        binding.mainIbSearch.setOnClickListener((v) ->
                startActivity(new Intent(getBaseContext(), SearchActivity.class))
        );

        binding.mainBtnNav.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.menue_chat) {
                getSupportFragmentManager().beginTransaction().replace(binding.mainFlContent.getId(), chatFragment).commit();
            }
            if (menuItem.getItemId() == R.id.menue_profile) {
                getSupportFragmentManager().beginTransaction().replace(binding.mainFlContent.getId(), profileFragment).commit();
            }
            return true;
        });


        binding.mainBtnNav.setSelectedItemId(R.id.menue_chat);



    }

    private void initializeFCMWithLogging() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d(TAG, "FCM Token: " + token);


                    if (FirebaseUtils.isLoggedIn()) {
                        FirebaseUtils.currentUserDetails()
                                .update("fcmToken", token)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        Log.d(TAG, "FCM token saved successfully to Firestore");
                                    } else {
                                        Log.e(TAG, "Failed to save FCM token: " + updateTask.getException());
                                    }
                                });
                    }
                });
    }
}