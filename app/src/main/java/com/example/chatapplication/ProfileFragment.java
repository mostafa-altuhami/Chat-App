package com.example.chatapplication;

import static com.example.chatapplication.other.Constants.UPLOAD_PRESET;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.chatapplication.Model.UserModel;
import com.example.chatapplication.Utils.AndroidUtil;
import com.example.chatapplication.Utils.FirebaseUtils;
import com.example.chatapplication.databinding.FragmentProfileBinding;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// profile fragment
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private String url;
    private UserModel profile;
    private Uri imageUri;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    // launcher to pick an image from the gallery
    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    imageUri = uri;

                    // load the image after choosing it
                    Glide.with(requireContext())
                            .load(uri)
                            .circleCrop()
                            .into(binding.fragmentProfileIv);
                }
    });

    public ProfileFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        setInProgress(false);
        getProfileInformation();

        binding.fragmentProfileUpdateBtn.setOnClickListener(view -> {
            // handle all states of the image
            if (imageUri != null) {
                executor.execute(() -> uploadToCloudinary(imageUri));

            } else if (profile.getImageUrl() != null && !profile.getImageUrl().isEmpty()) {
                executor.execute(this::updateProfileInfo);

            } else {
                AndroidUtil.showToast(requireContext(), "Please select a photo");
            }
        });

        // choose a picture when user clicks on the button
        binding.fragmentProfileIv.setOnClickListener(view -> pickImage.launch("image/*"));

        // logout button
        binding.fragmentProfileLogoutTv.setOnClickListener(view -> {
            FirebaseUtils.logout();
            // swap to login page again
            startActivity(new Intent(requireContext(), LoginPhoneNumberActivity.class));
        });
        return binding.getRoot();

    }


    // fun to update the profile info
    private void updateProfileInfo() {

        setInProgress(true);
        String newUsername = binding.fragmentProfileUsernameTv.getText().toString();
        // check if the user is valid or not
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            setInProgress(false);
            requireActivity().runOnUiThread(() -> binding.fragmentProfileUsernameTv.setError("Invalid Username"));
            return;
        }
        profile.setUsername(newUsername);
        profile.setImageUrl(url);
        // update the user info on firebase
        FirebaseUtils.currentUserDetails().set(profile).addOnSuccessListener( aVoid -> {
            setInProgress(false);
            AndroidUtil.showToast(getContext(), "Updated Successfully");
        })
        .addOnFailureListener(e -> {
            setInProgress(false);
            AndroidUtil.showToast(getContext(), "Failed to update");
        });

    }

    // fun to get profile info
    private void getProfileInformation() {
        FirebaseUtils.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                profile = task.getResult().toObject(UserModel.class);
                if (profile != null) {
                    requireActivity().runOnUiThread(() -> {
                        binding.fragmentProfileUsernameTv.setText(profile.getUsername());
                        binding.fragmentProfilePhoneTv.setText(profile.getPhone());
                        // check if there is an image
                        if (profile.getImageUrl() == null || profile.getImageUrl().isEmpty()) {
                            binding.fragmentProfileIv.setImageResource(R.drawable.user);
                        } else {
                            Glide.with(requireContext())
                                    .load(profile.getImageUrl())
                                    .circleCrop()
                                    .into(binding.fragmentProfileIv);
                        }
                    });
                }
            } else {
                AndroidUtil.showToast(requireContext(), "Failed to load profile");
            }
         });
    }

    // fun to upload the image to server
    private void uploadToCloudinary(Uri imageUri) {
        // upload the image
        MediaManager.get().upload(imageUri).unsigned(UPLOAD_PRESET).callback(new UploadCallback() {
            @Override
            public void onStart(String requestId) {
                Log.d("ProfileFragment", "Upload started: " + requestId);
            }

            @Override
            public void onProgress(String requestId, long bytes, long totalBytes) {
                Log.d("ProfileFragment", "In Progress ");
            }

            @Override
            public void onSuccess(String requestId, Map resultData) {
                url = (String) resultData.get("secure_url");

                updateProfileInfo();
            }

            @Override
            public void onError(String requestId, ErrorInfo error) {
                AndroidUtil.showToast(requireContext(), "Upload Failed : " + error.getDescription());
            }

            @Override
            public void onReschedule(String requestId, ErrorInfo error) {
                Log.w("ProfileFragment", "Upload rescheduled: " + error.getDescription());
            }
        }).dispatch();
    }

    // fun to control in progress bar
    private void setInProgress (boolean inProgress) {
        requireActivity().runOnUiThread(() -> {
            if (inProgress) {
                binding.fragmentProfilePb.setVisibility(View.VISIBLE);
                binding.fragmentProfileUpdateBtn.setVisibility(View.GONE);
            } else {
                binding.fragmentProfilePb.setVisibility(View.GONE);
                binding.fragmentProfileUpdateBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        executor.shutdownNow();
        binding = null;
    }
}
