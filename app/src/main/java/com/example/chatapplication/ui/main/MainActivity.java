package com.example.chatapplication.ui.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.chatapplication.ui.auth.LoginPhoneNumberActivity;
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

}