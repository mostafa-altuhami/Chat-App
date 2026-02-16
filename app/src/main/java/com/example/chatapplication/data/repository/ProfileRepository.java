package com.example.chatapplication.data.repository;

import static com.example.chatapplication.utils.Constants.UPLOAD_PRESET;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.example.chatapplication.data.model.UserModel;
import com.example.chatapplication.utils.FirebaseUtils;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class ProfileRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final MutableLiveData<String> toastMessage = new MutableLiveData<>();


    public LiveData<UserModel> getUserDetails() {
        MutableLiveData<UserModel> user = new MutableLiveData<>();

        db.collection("users")
                .document(FirebaseUtils.currentUserId())
                .get()
                .addOnSuccessListener( doc -> {
                    if (doc.exists()) {
                        user.setValue(doc.toObject(UserModel.class));
                    }
                })
                .addOnFailureListener(e ->
                        toastMessage.setValue("Failed to load user details"));

        return user;
    }

    public LiveData<Boolean> updateUserDetails(UserModel userModel) {
        MutableLiveData<Boolean> success = new MutableLiveData<>();

        db.collection("users")
                .document(FirebaseUtils.currentUserId())
                .set(userModel)
                .addOnSuccessListener(aVoid -> {
                    toastMessage.setValue("Updated successfully");
                    success.setValue(true);
                })
                .addOnFailureListener( e -> {
                    toastMessage.setValue("Failed to update user details");
                    success.setValue(false);
                });

        return success;

    }

    public LiveData<String> getToastMessage() {
        return toastMessage;
    }

    public LiveData<String> uploadProfileImage(Uri imageUrl) {
        MutableLiveData<String> result = new MutableLiveData<>();

        MediaManager.get()
                .upload(imageUrl)
                .unsigned(UPLOAD_PRESET)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                       String url = (String) resultData.get("secure_url");
                       result.postValue(url);
                       toastMessage.setValue("Uploaded Successfully");
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        result.postValue(null);
                        toastMessage.setValue("Upload Failed :" + error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();

        return result;
    }

}
