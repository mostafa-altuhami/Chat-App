package com.example.chatapplication;

import static android.content.ContentValues.TAG;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.chatapplication.Utils.AndroidUtil;
import com.example.chatapplication.Utils.FirebaseUtils;
import com.example.chatapplication.databinding.ActivityMainBinding;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private ChatFragment chatFragment;
    private ProfileFragment profileFragment;

    // launcher to request notifications permission
    private final ActivityResultLauncher<String> requestPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    AndroidUtil.showToast(this, "Permission Granted!");
                } else {
                    AndroidUtil.showToast(this, "Your App Can't receive any notification");
                }
            });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeFCMWithLogging();


        setSupportActionBar(binding.mainToolbar);

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();

        // Request notification permission
        requestNotificationPermission();

        binding.mainIbSearch.setOnClickListener((v) ->
                // swap to search activity without data
                startActivity(new Intent(getBaseContext(), SearchActivity.class))
        );

        // handle menue item state
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


    // fun to request Notifications permission
    private void requestNotificationPermission() {

        // handle status of the permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission
                    (this , Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Log.i("Granted", "Permission Granted");
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

                new AlertDialog.Builder(this)
                        .setTitle("Note!")
                        .setMessage("We need this permission to send you the recent message")
                        .setPositiveButton("Allow", (dialogInterface, i) ->
                                requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS))
                        .setNegativeButton("Cancel", (dialogInterface, i) ->
                                AndroidUtil.showToast(this, "Your App Can't receive any notification"))
                        .show();
            } else {
                requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

    }

    // fun to initializing FCM
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