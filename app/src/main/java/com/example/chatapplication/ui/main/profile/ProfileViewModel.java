package com.example.chatapplication.ui.main.profile;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.chatapplication.data.model.UserModel;
import com.example.chatapplication.data.repository.ProfileRepository;

public class ProfileViewModel extends ViewModel {

    private final ProfileRepository repository = new ProfileRepository();

    public LiveData<UserModel> getUserDetails() {
        return repository.getUserDetails();
    }

    public LiveData<Boolean> updateUserDetails(UserModel userModel) {
        return repository.updateUserDetails(userModel);
    }

    public LiveData<String> getToastMessage() {
        return repository.getToastMessage();
    }

    public LiveData<String> uploadProfileImage(Uri imageUrl) {
        return repository.uploadProfileImage(imageUrl);
    }


}
