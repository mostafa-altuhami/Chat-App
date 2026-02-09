package com.example.chatapplication.data.repository;

import android.app.Activity;

import androidx.annotation.NonNull;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AuthRepository {

    public interface AuthCallback {
        void onCodeSent(String verificationId);
        void onVerified();
        void onError(String error);
    }

    private final FirebaseAuth auth;
    private PhoneAuthProvider.ForceResendingToken resendToken;

    private static AuthRepository instance;
    private static final long TIMEOUT = 60L;

    public AuthRepository() {
        auth = FirebaseAuth.getInstance();
    }

    public static AuthRepository getRepositoryInstance() {
        if (instance == null) {
            instance = new AuthRepository();
        }
        return instance;
    }

    public void sendOtp(
            String phoneNumber,
            Activity activity,
            AuthCallback callback
    ) {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(TIMEOUT, TimeUnit.SECONDS)
                        .setActivity(activity)
                        .setCallbacks(getCallbacks(callback))
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void resendOtp(
            String phoneNumber,
            Activity activity,
            AuthCallback callback
    ) {

        if (resendToken == null) {
            callback.onError("Resend not available yet");
            return;
        }

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(TIMEOUT, TimeUnit.SECONDS)
                        .setActivity(activity)
                        .setCallbacks(getCallbacks(callback))
                        .setForceResendingToken(resendToken)
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void verifyOtp(
            String verificationId,
            String otp,
            AuthCallback callback
    ) {

        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(verificationId, otp);

        auth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onVerified();
                    } else {
                        callback.onError("Invalid OTP");
                    }
                });
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
    getCallbacks(AuthCallback callback) {

        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                auth.signInWithCredential(credential)
                        .addOnSuccessListener(authResult -> callback.onVerified())
                        .addOnFailureListener(e -> callback.onError(e.getMessage()));
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                callback.onError(e.getMessage());
            }

            @Override
            public void onCodeSent(
                    @NonNull String verificationId,
                    @NonNull PhoneAuthProvider.ForceResendingToken token
            ) {
                resendToken = token;
                callback.onCodeSent(verificationId);
            }
        };
    }
}