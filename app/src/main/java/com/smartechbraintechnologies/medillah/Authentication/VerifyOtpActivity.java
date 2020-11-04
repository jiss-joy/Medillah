package com.smartechbraintechnologies.medillah.Authentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;
import com.smartechbraintechnologies.medillah.MainActivity;
import com.smartechbraintechnologies.medillah.R;
import com.smartechbraintechnologies.medillah.ShowSnackbar;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VerifyOtpActivity extends AppCompatActivity {

    public static final long OTP_START_TIME_IN_MILLIS = 60000;

    private TextView otp_sent_tv, mCountDown_tv;
    private EditText otp_et;
    private Button submitBTN, resendBTN;
    private RelativeLayout relativeLayout;
    private CountDownTimer mCountDownTimer;

    private FirebaseAuth mAuth;

    private String mPhoneNumber;
    private ForceResendingToken token;
    private OnVerificationStateChangedCallbacks mCallback;
    private long mTimeLeftInMillis = OTP_START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        mPhoneNumber = getIntent().getStringExtra("Phone Number");

        initValues();
        updateCountDownText();
        sendOTP();


        resendBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
                resendOTP();
            }
        });


    }

    private void resendOTP() {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91" + mPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallback)
                .setForceResendingToken(token)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void sendOTP() {
        startTimer();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91" + mPhoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallback)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                resendBTN.setEnabled(true);
                resendBTN.setBackgroundColor(getResources().getColor(R.color.primary));
                resendBTN.setTextColor(getResources().getColor(R.color.secondary));
                mCountDown_tv.setVisibility(View.GONE);
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        mCountDown_tv.setText(timeLeftFormatted);
    }

    private void resetTimer() {
        mTimeLeftInMillis = OTP_START_TIME_IN_MILLIS;
        updateCountDownText();
        resendBTN.setEnabled(false);
        resendBTN.setBackgroundColor(getResources().getColor(R.color.secondary));
        resendBTN.setTextColor(getResources().getColor(R.color.primary));
        mCountDown_tv.setVisibility(View.VISIBLE);
        startTimer();
    }

    private void signInUser(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                                finish();
                                startActivity(new Intent(VerifyOtpActivity.this, ClientSurveyActivity.class)
                                        .putExtra("Phone Number", mPhoneNumber));
                            } else {
                                finish();
                                startActivity(new Intent(VerifyOtpActivity.this, MainActivity.class));
                            }
                        }
                    }
                });
    }


    private void initValues() {
        otp_sent_tv = findViewById(R.id.verify_otp_phone_number);
        otp_et = findViewById(R.id.verify_otp_otp);
        submitBTN = findViewById(R.id.verify_otp_verify_btn);
        resendBTN = findViewById(R.id.verify_otp_resend_otp_btn);
        resendBTN.setBackgroundColor(getResources().getColor(R.color.secondary));
        resendBTN.setTextColor(getResources().getColor(R.color.primary));
        resendBTN.setEnabled(false);
        otp_sent_tv.setText(getResources().getString(R.string.otpsentto) + " +91-" + mPhoneNumber);
        relativeLayout = findViewById(R.id.verify_otp_relative_layout);
        mCountDown_tv = findViewById(R.id.verify_otp_otp_timer);

        mAuth = FirebaseAuth.getInstance();
        setCallback();
    }

    private void setCallback() {
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInUser(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(VerifyOtpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                        Toast.makeText(VerifyOtpActivity.this, "Too many attempts detected.\n Please try again later", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(VerifyOtpActivity.this, PhoneAuthActivity.class));
            }

            @Override
            public void onCodeSent(@NonNull String sentOTP, @NonNull ForceResendingToken forceResendingToken) {
                super.onCodeSent(sentOTP, forceResendingToken);
                token = forceResendingToken;
                submitBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String userOTP = otp_et.getText().toString();
                        if (userOTP.isEmpty()) {
                            ShowSnackbar.show(VerifyOtpActivity.this, getResources().getString(R.string.entervalidotp),
                                    relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
                        } else if (userOTP.length() != 6) {
                            ShowSnackbar.show(VerifyOtpActivity.this, getResources().getString(R.string.entervalidotp),
                                    relativeLayout, getResources().getColor(R.color.red), getResources().getColor(R.color.white));
                        } else {
                            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(sentOTP, userOTP);
                            signInUser(phoneAuthCredential);
                        }
                    }
                });
            }
        };
    }
}