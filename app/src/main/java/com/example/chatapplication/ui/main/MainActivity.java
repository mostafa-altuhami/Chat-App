package com.example.chatapplication.ui.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.chatapplication.ui.auth.LoginPhoneNumberActivity;
import com.example.chatapplication.ui.auth.LoginUsernameActivity;
import com.example.chatapplication.ui.main.profile.ProfileFragment;
import com.example.chatapplication.R;
import com.example.chatapplication.ui.search.SearchActivity;
import com.example.chatapplication.databinding.ActivityMainBinding;
import com.example.chatapplication.ui.main.chats.ChatFragment;
import com.example.chatapplication.utils.FirebaseUtils;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ChatFragment chatFragment;
    private ProfileFragment profileFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        if (!FirebaseUtils.isLoggedIn()) {
            startActivity(new Intent(this, LoginPhoneNumberActivity.class));
            finish();
            return;
        }
        FirebaseUtils.checkProfileUsername(isCompleted -> {
            if (!isCompleted) {
                startActivity(new Intent(this, LoginUsernameActivity.class));
                finish();
            } else openMainContent();
        });
    }


    private void openMainContent() {

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();

        binding.mainBtnNav.setOnItemSelectedListener(menuItem -> {

            if (menuItem.getItemId() == R.id.menue_chat) {
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.mainFlContent.getId(), chatFragment)
                        .commit();
            }

            if (menuItem.getItemId() == R.id.menue_profile) {
                getSupportFragmentManager().beginTransaction()
                        .replace(binding.mainFlContent.getId(), profileFragment)
                        .commit();
            }

            return true;
        });

        binding.mainBtnNav.setSelectedItemId(R.id.menue_chat);

        binding.fabBtn.setOnClickListener(view ->
                startActivity(new Intent(this, SearchActivity.class))
        );
    }


}