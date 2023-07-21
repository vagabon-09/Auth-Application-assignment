package com.sharebysocial.authapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sharebysocial.authapplication.Fragment.ForgotPassword;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    EditText loginEmail, loginPassword;
    CardView loginBtn;

    FirebaseAuth mAuth;
    TextView createAccount, forgotPassword;
    String TAG = "loginActivityPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginEmail = findViewById(R.id.LoginEmailId);
        loginPassword = findViewById(R.id.LoginPasswordId);
        loginBtn = findViewById(R.id.LoginRegisterBtnId);
        createAccount = findViewById(R.id.LoginCreateNewAccountId);
        forgotPassword = findViewById(R.id.logInforgotPasswordId);
        mAuth = FirebaseAuth.getInstance();
        clickLoginBtn(); // when click on login button
        clickCreatBtn();
        forgotButton();

    }

    private void forgotButton() {
        forgotPassword.setOnClickListener(v -> {
            ForgotPassword bottomSheetFragment = new ForgotPassword();
            bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
        });
    }

    private void clickCreatBtn() {
        createAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void clickLoginBtn() {
        loginBtn.setOnClickListener(v -> {
            String l_email = loginEmail.getText().toString();
            String l_password = loginPassword.getText().toString();
            if (l_email.equals("") || l_password.equals("")) {
                Toast.makeText(LoginActivity.this, "Fill all empty section", Toast.LENGTH_SHORT).show();
            } else {
                loginAccount(l_email, l_password);
            }
        });
    }

    @Override
    protected void onStart() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            reload();
        }

        super.onStart();
    }

    private void reload() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    public void loginAccount(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseUser user = mAuth.getCurrentUser();
                        SharedPreferences preferences = getSharedPreferences("signingMethod", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("loginMethod", "email");
                        editor.apply();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}