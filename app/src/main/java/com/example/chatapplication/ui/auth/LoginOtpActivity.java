package com.example.chatapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.chatapplication.R;
import com.example.chatapplication.databinding.ActivityLoginOtpBinding;
import com.example.chatapplication.ui.main.MainActivity;
import com.example.chatapplication.utils.AndroidUtil;

public class LoginOtpActivity extends AppCompatActivity {

    private ActivityLoginOtpBinding binding;

    private AuthViewModel viewModel;
    String  phoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        phoneNumber = getIntent().getStringExtra("phone");
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        observeState();
        observeTimer();
        observeToast();

        viewModel.sendOtp(phoneNumber, this);

        binding.otpBtnNext.setOnClickListener(view -> {
            String enteredOtp = binding.otpEtOtp.getText().toString();

            viewModel.verifyOtp(enteredOtp) ;
        });


        binding.otpTvResend.setOnClickListener(view -> viewModel.resendOtp(phoneNumber, this));

    }


    private void observeState() {
        viewModel.getUiState().observe(this, state -> {

            switch (state.status) {

                case LOADING:
                    setLoading(true);
                    break;

                case CODE_SENT:
                    setLoading(false);
                    break;

                case VERIFIED:
                    setLoading(false);
                    Intent intent = new Intent(this , MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;

                case ERROR:
                    setLoading(false);
                    binding.otpEtOtp.setError(state.message);
                    break;
            }
        });
    }

    private void observeTimer() {
        viewModel.getTimerText().observe(this, leftSeconds -> {
            if (leftSeconds > 0)
                binding.otpTvResend.setText(getString(R.string.resend_the_otp_in, leftSeconds));
            else
                binding.otpTvResend.setText(R.string.resend_the_otp1);
        });

        viewModel.getIsResendEnabled().observe(this , isEnabled ->
                binding.otpTvResend.setEnabled(isEnabled));
    }

    private void observeToast() {
        viewModel.getToastMessage().observe(this, message ->
                AndroidUtil.showToast(this, message));
    }

    private void setLoading(boolean loading) {
        binding.otpPb.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.otpBtnNext.setVisibility(loading ? View.GONE : View.VISIBLE);
    }

}