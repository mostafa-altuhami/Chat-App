package com.example.chatapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.window.SplashScreenView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chatapplication.Model.UserModel;
import com.example.chatapplication.Utils.AndroidUtil;
import com.example.chatapplication.Utils.FirebaseUtils;
import com.example.chatapplication.databinding.ActivitySplashScreenBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Objects;


// Custom Splash screen
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private ActivitySplashScreenBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            getSplashScreen().setOnExitAnimationListener(SplashScreenView::remove);
        }
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // handle open app behavior when the user click on the notification
        if (FirebaseUtils.isLoggedIn() && getIntent().getExtras() != null) {
            String userId = getIntent().getExtras().getString("userId");
            FirebaseUtils.allCollectionReference().document(Objects.requireNonNull(userId))
                    .get().addOnSuccessListener(task -> {

                        if (task.exists()) {
                            UserModel userModel = task.toObject(UserModel.class);

                            if (userModel != null) {
                                Intent mainIntent = new Intent(this, MainActivity.class);
                                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(mainIntent);

                                Intent intent = new Intent(this, ChatActivity.class);
                                AndroidUtil.passUserModelAsIntent(intent, Objects.requireNonNull(userModel));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                                finish();
                            } else {
                                Log.e("SplashScreen", "User model is null");
                                openMain();
                            }
                        }  else {
                            Log.e("SplashScreen", "User document does not exist");
                            openMain();
                        }
            }).addOnFailureListener(e -> {
                        Log.e("SplashScreen", "Failed to get user info", e);
                        AndroidUtil.showToast(this, "Failed to load user data");
            });
            // if user open the app normally
        } else {
            // if it's logged open main else open login activity
            if (FirebaseUtils.isLoggedIn())
                preloadedChats();
            else {
                startActivity(new Intent(SplashActivity.this, LoginPhoneNumberActivity.class));
                finish();
            }
        }


    }

    // fun to load all chats
    public void preloadedChats() {
        FirebaseFirestore.getInstance()
                .collection("chats")
                .get()
                .addOnCompleteListener(task -> openMain());
    }

    // fun to open main activity
    private void openMain() {
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }
}