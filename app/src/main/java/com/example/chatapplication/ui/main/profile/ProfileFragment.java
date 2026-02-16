package com.example.chatapplication.ui.main.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;
import com.example.chatapplication.data.model.UserModel;
import com.example.chatapplication.R;
import com.example.chatapplication.utils.AndroidUtil;
import com.example.chatapplication.utils.FirebaseUtils;
import com.example.chatapplication.databinding.FragmentProfileBinding;
import com.example.chatapplication.ui.auth.LoginPhoneNumberActivity;


public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private String url;
    private UserModel profile;
    private ProfileViewModel viewModel;
    private Uri imageUri;
    private boolean arrowFlag = true;

    private ActivityResultLauncher<String> pickImage;

    public ProfileFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        pickImage =
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

        setInProgress(false);
        observeToast();
        getProfileInformation();

        binding.fragmentProfileUpdateBtn.setOnClickListener(view -> {
            if (imageUri != null) {
                uploadToCloudinary(imageUri);
            } else if (profile.getImageUrl() != null && !profile.getImageUrl().isEmpty()) {
                updateProfileInfo();
            } else {
                binding.fragmentProfileIv.setImageResource(R.drawable.ic_person);
                updateProfileInfo();
            }
        });

        binding.ivAccountArrow.setOnClickListener(view -> {
            if (arrowFlag) {
                binding.profileUpdateUsername.setVisibility(View.VISIBLE);
                binding.fragmentProfileUpdateBtn.setVisibility(View.VISIBLE);
                binding.ivAccountArrow.setImageResource(R.drawable.ic_arrow_up);
            } else {
                binding.profileUpdateUsername.setVisibility(View.GONE);
                binding.ivAccountArrow.setImageResource(R.drawable.ic_arrow_down);
            }
            arrowFlag = !arrowFlag;

        });

        // choose a picture when user clicks on the button
        binding.fragmentProfileIv.setOnClickListener(view -> {
                pickImage.launch("image/*");

        });

        binding.fragmentProfileLogoutTv.setOnClickListener(view -> {
            FirebaseUtils.logout();
            Intent intent = new Intent(requireContext(), LoginPhoneNumberActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        return binding.getRoot();

    }


    private void updateProfileInfo() {

        setInProgress(true);
        String newUsername = binding.profileUpdateUsername.getText().toString();
        // check if the user is valid or not
        if (newUsername.isEmpty() || newUsername.length() < 3) {
            setInProgress(false);
            binding.fragmentProfileUsernameTv.setError("Invalid Username");
            return;
        }
        binding.fragmentProfileUsernameTv.setText(newUsername);
        profile.setUsername(newUsername);
        profile.setImageUrl(url);

        viewModel.updateUserDetails(profile).observe(getViewLifecycleOwner(), success ->
                setInProgress(false));

    }

    private void getProfileInformation() {
        viewModel.getUserDetails().observe( getViewLifecycleOwner() ,userModel -> {
            if (userModel != null) {
                profile = userModel;
                binding.fragmentProfileUsernameTv.setText(profile.getUsername());
                binding.profileUpdateUsername.setText(profile.getUsername());
                binding.fragmentProfilePhoneTv.setText(profile.getPhone());
                if (profile.getImageUrl() == null || profile.getImageUrl().isEmpty()) {
                    binding.fragmentProfileIv.setImageResource(R.drawable.ic_person);
                } else {
                    Glide.with(requireContext())
                            .load(profile.getImageUrl())
                            .circleCrop()
                            .into(binding.fragmentProfileIv);
                }
            }
        });

    }

    private void uploadToCloudinary(Uri imageUri) {

        viewModel.uploadProfileImage(imageUri).observe(getViewLifecycleOwner(), imageUrl-> {
            if (imageUrl != null) {
                url = imageUrl;
                updateProfileInfo();
            }else
                setInProgress(false);
        });


    }

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

    private void observeToast () {
        viewModel.getToastMessage().observe(getViewLifecycleOwner(), message ->
                AndroidUtil.showToast(requireContext(), message));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
