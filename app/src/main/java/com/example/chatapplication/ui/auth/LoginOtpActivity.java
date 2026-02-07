package com.example.chatapplication.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.chatapplication.R;
import com.example.chatapplication.databinding.ActivityLoginOtpBinding;
import com.example.chatapplication.ui.main.MainActivity;

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
                    startResendTimer();
                    break;

                case VERIFIED:
                    setLoading(false);
                    Intent intent = new Intent(this , MainActivity.class);
                    startActivity(intent);
                    break;

                case ERROR:
                    setLoading(false);
                    binding.otpEtOtp.setError(state.message);
                    break;
            }
        });
    }

    private void setLoading(boolean loading) {
        binding.otpPb.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.otpBtnNext.setVisibility(loading ? View.GONE : View.VISIBLE);
    }


    void startResendTimer() {
        binding.otpTvResend.setEnabled(false);


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