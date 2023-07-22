package com.sharebysocial.authapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText otpInput;
    String numTemp = "";
    TextView requestOTP;
    ProgressBar progressBar;
    Button otpVerify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);
        otpInput = findViewById(R.id.otp_inputId);
        Intent intent = getIntent();
        String mobileNumber = intent.getStringExtra("mobileNumber");
        Log.d("mobileNumber", "onCreate: " + mobileNumber);
        mAuth = FirebaseAuth.getInstance();
        manualLogin();
        requestOTP = findViewById(R.id.requestOtpId);
        progressBar = findViewById(R.id.circularProgressBar);
        getOTP();
        sendOtp(mobileNumber);
    }

    private void getOTP() {
        requestOTP.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), PhoneActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void manualLogin() {

        otpVerify = findViewById(R.id.verifyBtnId);
        otpVerify.setOnClickListener(v -> {
            Log.d("otp", "manualLogin: " + otpInput.getText().toString());
            if (otpInput.getText().toString().equals("") || otpInput.getText().toString().length() < 6) {
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show();
            } else {
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(numTemp, otpInput.getText().toString());
                signInWithPhoneAuthCredential(credential);
            }

        });
    }

    private void sendOtp(String mobileNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(mobileNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                otpVerify.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                numTemp = s;
                                Log.d("tempNum", "onCodeSent: " + numTemp);
                                super.onCodeSent(s, forceResendingToken);
                            }

                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Log.d("mobileAuthException", "onVerificationFailed: " + e.toString());
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signInCredential", "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            progressBar.setVisibility(View.GONE);
                            startActivity(intent);
                            finish();

                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("phoneCredential", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(getApplicationContext(), "Invalid verification code", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}