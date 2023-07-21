package com.sharebysocial.authapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    TextView emailStatus;
    Button LogoutBtn;
    Button resendEmail;
    FirebaseAuth mAuth;
    String loginMethod = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        emailStatus = findViewById(R.id.home_emailVerificationId);
        LogoutBtn = findViewById(R.id.home_logoutBtn);
        resendEmail = findViewById(R.id.home_emailResendId);
        checkLoginMethod(); // Checking login method
        logOutBtn(); //Logout button when clicked
        checkEmail(); // checking email function
        ResendEmail(); // Resend verification email

    }

    private void checkLoginMethod() {
        SharedPreferences preferences = getSharedPreferences("signingMethod", MODE_PRIVATE);
        loginMethod = preferences.getString("loginMethod", "");
        Log.d("loginMethod", "checkLoginMethod: " + loginMethod.toString());
    }

    private void logOutBtn() {
        LogoutBtn.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void ResendEmail() {
        resendEmail.setOnClickListener(v -> {
            Objects.requireNonNull(mAuth.getCurrentUser()).sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(HomeActivity.this, "Email verification link is send to your email", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    @Override
    protected void onResume() {
        checkEmail();
        super.onResume();
    }

    private void checkEmail() {
        if (loginMethod.equals("email")) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                user.reload().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean isEmailVerified = user.isEmailVerified();
                        if (isEmailVerified) {
                            emailStatus.setText("Email is verified");
                            resendEmail.setVisibility(View.INVISIBLE);
                        } else {
                            emailStatus.setText("Email is not verified please verify now");
                            resendEmail.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Log.d("checkEmail", "Error reloading user: " + task.getException());
                    }

                });
            }
        }


    }
}