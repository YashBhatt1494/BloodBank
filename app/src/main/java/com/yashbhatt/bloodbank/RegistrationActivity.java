package com.yashbhatt.bloodbank;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.yashbhatt.bloodbank.fragments.SignInFragment;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegistrationActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        findViewById(R.id.text_btn_signup_email).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.text_btn_signup_email) {
            signUpWithEmail();
        }

    }

    private void signUpWithEmail() {
        openFragment(new SignInFragment(), SignInFragment.TAG);
    }

    public void openFragment(Fragment fragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.registration_fragments_container, fragment)
                .addToBackStack(tag)
                .commit();
    }
}
