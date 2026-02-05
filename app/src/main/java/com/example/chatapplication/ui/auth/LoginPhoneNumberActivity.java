package com.example.chatapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapplication.databinding.ActivityLoginPhoneNumberBinding;


public class LoginPhoneNumberActivity extends AppCompatActivity {
    private ActivityLoginPhoneNumberBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.phonePb.setVisibility(View.GONE);


        binding.phoneCpCountryCode.registerCarrierNumberEditText(binding.phoneEtNumber);

        binding.phoneBtnSendOtp.setOnClickListener(view -> {
            if (!binding.phoneCpCountryCode.isValidFullNumber()) {
                binding.phoneEtNumber.setError("Phone number is not valid");
                return;
            }

            Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginOtpActivity.class);
            intent.putExtra("phone", binding.phoneCpCountryCode.getFullNumberWithPlus());
            startActivity(intent);
        });

    }
}