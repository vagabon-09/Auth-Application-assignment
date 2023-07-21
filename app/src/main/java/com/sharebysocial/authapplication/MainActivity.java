package com.sharebysocial.authapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    String TAG;
    CardView signUpBtn;
    EditText email, password, rePassword;
    LinearLayout singUpPhoneNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TAG = "mainActivitiesTag";
        //Hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //signUp button
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.signUpEmailId);
        password = findViewById(R.id.signUpPasswordId);
        rePassword = findViewById(R.id.renterPasswordId);
        signUpBtn = findViewById(R.id.signUpRegisterBtnId);
        registerBtn(); // When click on register btn
        alreadyHaveAccount(); // when click on already click button
        signWithPhone();
    }

    private void signWithPhone() {
        singUpPhoneNumber = findViewById(R.id.signupCallId);
        singUpPhoneNumber.setOnClickListener(v -> {
            Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), PhoneActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void alreadyHaveAccount() {
        TextView AlreadyAccountBtn = findViewById(R.id.SignupAlreadyAccountId);
        AlreadyAccountBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void registerBtn() {
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s_email = email.getText().toString();
                String s_password = password.getText().toString();
                String s_rePassword = rePassword.getText().toString();
                if (!s_email.equals("")) {
                    if (s_rePassword.equals(s_password) && !s_password.equals("")) {
                        createAccount(s_email, s_password);
                    } else {
                        Toast.makeText(MainActivity.this, "Password is not matched or Password is empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please enter email", Toast.LENGTH_SHORT).show();
                }

            }

        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            reload();
        }

    }

    private void reload() {
        Intent intent = new Intent(this,HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            currentUser = mAuth.getCurrentUser();
                            assert currentUser != null;
                            currentUser.sendEmailVerification().addOnSuccessListener(unused -> Toast.makeText(MainActivity.this, "Successfully verification email send to your mail.", Toast.LENGTH_SHORT).show());
                            updateUI(currentUser);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void updateUI(FirebaseUser user) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
