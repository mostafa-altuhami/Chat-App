package com.example.chatapplication.ui.auth;

import android.app.Activity;
import android.os.CountDownTimer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.chatapplication.data.repository.AuthRepository;

public class AuthViewModel extends ViewModel {

    private  final AuthRepository repository;
    private final MutableLiveData<AuthUiState> uiState;
    private final MutableLiveData<Long> timerText;
    private final MutableLiveData<Boolean> isResendEnabled;
    private final MutableLiveData<String> toastMessage;
    private CountDownTimer countDownTimer;
    private String verificationId;
    private boolean otpSent = false;

    public AuthViewModel() {
        repository = AuthRepository.getRepositoryInstance();
        uiState = new MutableLiveData<>(AuthUiState.idle());
        timerText = new MutableLiveData<>();
        isResendEnabled = new MutableLiveData<>();
        toastMessage = new MutableLiveData<>();
    }

    public LiveData<AuthUiState> getUiState() {
        return uiState;
    }
    public LiveData<Long> getTimerText() { return timerText; }
    public LiveData<Boolean> getIsResendEnabled() { return isResendEnabled; }
    public LiveData<String> getToastMessage() { return toastMessage; }

    public void sendOtp(String phone, Activity activity) {
        if (otpSent) return;

        uiState.setValue(AuthUiState.loading());

        repository.sendOtp(phone, activity, new AuthRepository.AuthCallback() {
            @Override
            public void onCodeSent(String id) {
                verificationId = id;
                otpSent = true;
                uiState.setValue(AuthUiState.codeSent());
                startResendTimer();
                toastMessage.setValue("OTP sent successfully");
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

    public void resendOtp(String phone, Activity activity) {
        uiState.setValue(AuthUiState.loading());

        repository.resendOtp(phone, activity, new AuthRepository.AuthCallback() {
            @Override
            public void onCodeSent(String id) {
                verificationId = id;
                uiState.setValue(AuthUiState.codeSent());
                startResendTimer();
                toastMessage.setValue("OTP sent successfully");
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
                toastMessage.setValue("Welcome!");
            }

            @Override
            public void onError(String error) {
                uiState.setValue(AuthUiState.error(error));
            }
        });
    }

    private void startResendTimer() {
        isResendEnabled.setValue(false);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }


        countDownTimer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                timerText.setValue(l / 1000);
            }

            @Override
            public void onFinish() {
                isResendEnabled.setValue(true);
                timerText.setValue(0L);
            }
        }.start();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}