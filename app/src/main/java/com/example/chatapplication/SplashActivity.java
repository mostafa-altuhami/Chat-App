package com.example.chatapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chatapplication.Model.UserModel;
import com.example.chatapplication.Utils.AndroidUtil;
import com.example.chatapplication.Utils.FirebaseUtils;
import com.example.chatapplication.databinding.ActivitySplashScreenBinding;

// Custom Splash screen
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private ActivitySplashScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // handle open app behavior when the user click on the notification
        if (FirebaseUtils.isLoggedIn()
                && getIntent() != null
                && getIntent().hasExtra("userId")) {

            String userId = getIntent().getStringExtra("userId");

            FirebaseUtils.allCollectionReference()
                    .document(userId)
                    .get()
                    .addOnSuccessListener(task -> {
                        if (task.exists()) {
                            UserModel userModel = task.toObject(UserModel.class);
                            if (userModel != null) {
                                Intent mainIntent = new Intent(this, MainActivity.class);
                                startActivity(mainIntent);

                                Intent chatIntent = new Intent(this, ChatActivity.class);
                                AndroidUtil.passUserModelAsIntent(chatIntent, userModel);
                                startActivity(chatIntent);

                                finish();
                            } else {
                                openMain();
                            }
                        } else {
                            openMain();
                        }
                    })
                    .addOnFailureListener(e -> openMain());

        } else {
            if (FirebaseUtils.isLoggedIn()) {
                openMain();
            } else {
                startActivity(new Intent(this, LoginPhoneNumberActivity.class));
                finish();
            }
        }

    }


    // fun to open main activity
    private void openMain() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
}