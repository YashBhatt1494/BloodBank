package com.yashbhatt.bloodbank.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.yashbhatt.bloodbank.BloodBank;
import com.yashbhatt.bloodbank.MainActivity;
import com.yashbhatt.bloodbank.R;
import com.yashbhatt.bloodbank.global.Constants;
import com.yashbhatt.bloodbank.global.SessionManager;
import com.yashbhatt.bloodbank.global.Utility;

import java.util.HashMap;

public class LoginFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "LoginFragment";
    private View loginView;

    private EditText editEmail, editPass;
    private Button btnLogin;
    private FirebaseFirestore mFirestore;
    private SessionManager session;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (loginView == null) {
            loginView = inflater.inflate(R.layout.fragment_login, container, false);
        }

        editEmail = loginView.findViewById(R.id.edit_email);
        editPass = loginView.findViewById(R.id.edit_password);
        btnLogin = loginView.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        mFirestore = ((BloodBank) getActivity().getApplication()).getFirestore();
        session = ((BloodBank) getActivity().getApplication()).getSession();
        return loginView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {
            login(editEmail.getText().toString(), editPass.getText().toString().trim());
        }
    }

    private void login(final String email, String pass) {
        mFirestore.collection(Constants.COLLECTION_PROFILE)
                .whereEqualTo(Constants.PROFILE_EMAIL, email)
                .whereEqualTo(Constants.PROFILE_PASS, pass)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            if (task.getResult().getDocuments().size() >= 1) {
                                Utility.show(getView(), "Login Success");

                                HashMap<String, Object> userObj = (HashMap<String, Object>) task.getResult()
                                        .getDocuments().get(0).getData();
                                session.setEmail(email);
                                session.setFirstName(String.valueOf(userObj.get(Constants.PROFILE_FNAME)));
                                session.setLastName(String.valueOf(userObj.get(Constants.PROFILE_LNAME)));

                                Intent dashboardIntent = new Intent(getContext(), MainActivity.class);
                                dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(dashboardIntent);
                            } else {

                                Utility.show(getView(), "Login failed");
                            }
                        } else {
                            Utility.show(getView(), "Login failed");
                        }

                    }
                });
    }
}
