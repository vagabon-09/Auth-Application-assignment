package com.sharebysocial.authapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

public class PhoneActivity extends AppCompatActivity {
    CountryCodePicker ccp;
    EditText numPh;
    LinearLayout getOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        ccp = findViewById(R.id.ccp);
        numPh = findViewById(R.id.phoneNumberId);
        getOtp = findViewById(R.id.getOtpBtn);
        ccp.registerCarrierNumberEditText(numPh);
        alreadyAccount(); // Already have account bth
        getOtpBtn(); // when clicked on get otp button
    }

    private void getOtpBtn() {
        getOtp.setOnClickListener(v -> {
            if (!numPh.getText().toString().equals("")) {
                Intent intent = new Intent(getApplicationContext(), OTPActivity.class);
                intent.putExtra("mobileNumber", ccp.getFullNumberWithPlus().replace(" ", ""));
                startActivity(intent);
                SharedPreferences preferences = getSharedPreferences("signingMethod", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("loginMethod", "phone");
                editor.apply();
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Please enter phone number first then try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void alreadyAccount() {
        TextView accountBtn = findViewById(R.id.ph_alreadyAccount);
        accountBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplication(), LoginActivity.class);
            startActivity(intent);
        });
    }
}