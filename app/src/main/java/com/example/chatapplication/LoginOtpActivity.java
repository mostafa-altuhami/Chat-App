package com.example.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chatapplication.Utils.AndroidUtil;
import com.example.chatapplication.databinding.ActivityLoginOtpBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;



// activity for the OTP verification
public class LoginOtpActivity extends AppCompatActivity {

    private ActivityLoginOtpBinding binding;
    FirebaseAuth mAuth;
    Long timeout = 60L;

    String verificationCode, phoneNumber;
    PhoneAuthProvider.ForceResendingToken resendingToken;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // get the phone number from the intent
        phoneNumber = getIntent().getExtras().getString("phone");


        mAuth = FirebaseAuth.getInstance();

        sendOtp(phoneNumber, false);

        binding.otpBtnNext.setOnClickListener(view -> {
            String enteredOtp = binding.otpEtOtp.getText().toString();

            // check if the entered OTP is true or not
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, enteredOtp);
            signIn(credential);
            setInProgress(true);
        });


        binding.otpTvResend.setOnClickListener(view -> sendOtp(phoneNumber, true));

    }

    // fun to sent the OTP to to the user
    void sendOtp(String phoneNumber, boolean isResend) {
        startResendTimer();
        setInProgress(true);

        // send the OTP to the user
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeout, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signIn(phoneAuthCredential);
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        AndroidUtil.showToast(getBaseContext(), "OTP verification failed");
                        setInProgress(false);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationCode = s;
                        resendingToken = forceResendingToken;
                        AndroidUtil.showToast(getBaseContext(), "OTP sent successfully");
                        setInProgress(false);
                    }
                });

        // handle resend OTP
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        } else {
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    // fun to control in progress bar
    void setInProgress (boolean inProgress) {
        if (inProgress) {
            binding.otpPb.setVisibility(View.VISIBLE);
            binding.otpBtnNext.setVisibility(View.GONE);
        } else {
            binding.otpPb.setVisibility(View.GONE);
            binding.otpBtnNext.setVisibility(View.VISIBLE);
        }
    }

    void signIn (PhoneAuthCredential credential) {
        setInProgress(true);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            setInProgress(false);
            // check if the task is completed successfully
            if (task.isSuccessful()){
                // swap to the username activity with the phone number
                Intent intent = new Intent(LoginOtpActivity.this, LoginUsernameActivity.class);
                intent.putExtra("phone", phoneNumber);
                startActivity(intent);
                finish();
            } else {
                AndroidUtil.showToast(getBaseContext(), "OTP verification failed");
            }
        });
    }

    // fun to calculate the amount of seconds
    void startResendTimer() {
        binding.otpTvResend.setEnabled(false);


        // start the counter
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                long leftSeconds = l / 1000;
                binding.otpTvResend.setText(getString(R.string.resend_the_otp_in, leftSeconds));
            }

            @Override
            public void onFinish() {
                binding.otpTvResend.setEnabled(true);
                binding.otpTvResend.setText(R.string.resend_the_otp1);
            }
        }.start();
    }
}