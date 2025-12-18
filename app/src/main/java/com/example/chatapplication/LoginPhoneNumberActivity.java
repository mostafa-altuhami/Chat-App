package com.example.chatapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.chatapplication.databinding.ActivityLoginPhoneNumberBinding;


// activity for register the phone number
public class LoginPhoneNumberActivity extends AppCompatActivity {
    private ActivityLoginPhoneNumberBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.phonePb.setVisibility(View.GONE);


        binding.phoneCpCountryCode.registerCarrierNumberEditText(binding.phoneEtNumber);

        // send the OTP to the user
        binding.phoneBtnSendOtp.setOnClickListener(view -> {
            // check if the number is invalid
            if (!binding.phoneCpCountryCode.isValidFullNumber()) {
                binding.phoneEtNumber.setError("Phone number is not valid");
                return;
            }

            // go to OTP activity with the phone number
            Intent intent = new Intent(LoginPhoneNumberActivity.this, LoginOtpActivity.class);
            intent.putExtra("phone", binding.phoneCpCountryCode.getFullNumberWithPlus());
            startActivity(intent);
        });

    }
}