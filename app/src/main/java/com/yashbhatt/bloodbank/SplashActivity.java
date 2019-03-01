package com.yashbhatt.bloodbank;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yashbhatt.bloodbank.global.SessionManager;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = "SplashActivity";

    private SessionManager session;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        session = ((BloodBank) getApplication()).getSession();
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        validateUser();
                    }
                }, 1500);
    }

    private void validateUser() {

        if (session.getEmail() == null) {
            Intent registrationIntent = new Intent(this, RegistrationActivity.class);
            registrationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(registrationIntent);

        } else {
            Intent dashboardIntent = new Intent(this, MainActivity.class);
            dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dashboardIntent);

        }
    }
}
