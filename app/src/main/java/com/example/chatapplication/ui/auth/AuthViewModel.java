package com.example.chatapplication.ui.auth;

import android.app.Activity;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chatapplication.data.repository.AuthRepository;
import com.example.chatapplication.utils.AndroidUtil;

public class AuthViewModel extends ViewModel {

    private final AuthRepository repository;
    private final MutableLiveData<AuthUiState> uiState;
    private String verificationId;
    private boolean otpSent = false;

    public AuthViewModel() {
        repository = new AuthRepository();
        uiState = new MutableLiveData<>(AuthUiState.idle());
    }

    public LiveData<AuthUiState> getUiState() {
        return uiState;
    }

    public void sendOtp(String phone, Activity activity) {
        if (otpSent) return;

        uiState.setValue(AuthUiState.loading());

        repository.sendOtp(phone, activity, new AuthRepository.AuthCallback() {
            @Override
            public void onCodeSent(String id) {
                verificationId = id;
                otpSent = true;
                uiState.setValue(AuthUiState.codeSent());
                AndroidUtil.showToast(activity.getBaseContext(), "OTP sent successfully");
            }

            @Override
            public void onVerified() {
                uiState.setValue(AuthUiState.verified());
                AndroidUtil.showToast(activity.getBaseContext(), "Welcome!");
            }

            @Override
            public void onError(String error) {
                uiState.setValue(AuthUiState.error(error));
            }
        });
    }

    public void resendOtp(String phone, Activity activity) {
        uiState.setValue(AuthUiState.loading());

        repository.resendOtp(phone, activity, new AuthRepository.AuthCallback() {
            @Override
            public void onCodeSent(String id) {
                verificationId = id;
                uiState.setValue(AuthUiState.codeSent());
                AndroidUtil.showToast(activity.getBaseContext(), "OTP resent successfully");
            }

            @Override
            public void onVerified() {
                uiState.setValue(AuthUiState.verified());
            }

            @Override
            public void onError(String error) {
                uiState.setValue(AuthUiState.error(error));
            }
        });
    }

    public void verifyOtp(String otp) {
        if (verificationId == null) {
            uiState.setValue(AuthUiState.error("OTP not sent"));
            return;
        }

        uiState.setValue(AuthUiState.loading());

        repository.verifyOtp(verificationId, otp, new AuthRepository.AuthCallback() {
            @Override
            public void onCodeSent(String verificationId) {}

            @Override
            public void onVerified() {
                uiState.setValue(AuthUiState.verified());
            }

            @Override
            public void onError(String error) {
                uiState.setValue(AuthUiState.error(error));
            }
        });
    }
}