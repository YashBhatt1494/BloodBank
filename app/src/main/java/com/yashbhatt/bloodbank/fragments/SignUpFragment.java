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
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.yashbhatt.bloodbank.BloodBank;
import com.yashbhatt.bloodbank.MainActivity;
import com.yashbhatt.bloodbank.R;
import com.yashbhatt.bloodbank.global.Constants;
import com.yashbhatt.bloodbank.global.SessionManager;
import com.yashbhatt.bloodbank.global.Utility;

import java.util.HashMap;

public class SignUpFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "SignUpFragment";
    private View registerView;

    private EditText editFname, editLname, editEmail, editPass, editAge;
    private Spinner spBlood;
    private ProgressBar progress;
    private Button btnSignup;
    private FirebaseFirestore mFirestore;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (registerView == null) {
            registerView = inflater.inflate(R.layout.fragment_signup, container, false);
        }

        editFname = registerView.findViewById(R.id.edit_fname);
        editLname = registerView.findViewById(R.id.edit_lname);
        editAge = registerView.findViewById(R.id.edit_age);
        editEmail = registerView.findViewById(R.id.edit_email);
        editPass = registerView.findViewById(R.id.edit_password);
        spBlood = registerView.findViewById(R.id.spinner_blood_group);
        btnSignup = registerView.findViewById(R.id.btn_signup);
        btnSignup.setOnClickListener(this);
        progress = registerView.findViewById(R.id.progress_sign_up);
        mFirestore = ((BloodBank) getActivity().getApplication()).getFirestore();
        session = ((BloodBank) getActivity().getApplication()).getSession();

        return registerView;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_signup) {
            signup();
        }
    }

    private void enableDisableElements(boolean flag) {
        if (!flag) {
            btnSignup.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
        } else {
            btnSignup.setVisibility(View.VISIBLE);
            progress.setVisibility(View.GONE);
        }

        editAge.setEnabled(flag);
        editFname.setEnabled(flag);
        editLname.setEnabled(flag);
        editEmail.setEnabled(flag);
        editPass.setEnabled(flag);
        spBlood.setEnabled(flag);
    }

    private void signup() {
        final String fname = editFname.getText().toString();
        String lname = editLname.getText().toString();
        final String email = editEmail.getText().toString();
        String pass = editPass.getText().toString();
        String age = editAge.getText().toString();
        String bloodGroup = spBlood.getSelectedItem().toString();

        if (fname.isEmpty()) {
            Utility.show(getView(), "First Name Required");
        } else if (lname.isEmpty()) {
            Utility.show(getView(), "Last Name Required");
        } else if (email.isEmpty()) {
            Utility.show(getView(), "Email is required");
        } else if (age.isEmpty()) {
            Utility.show(getView(), "Age required");
        } else {

            if (getActivity() != null) {
                final HashMap<String, Object> userObj = new HashMap<>();
                userObj.put(Constants.PROFILE_FNAME, fname);
                userObj.put(Constants.PROFILE_LNAME, lname);
                userObj.put(Constants.PROFILE_EMAIL, email);
                userObj.put(Constants.PROFILE_AGE, age);
                userObj.put(Constants.PROFILE_PASS, pass);
                userObj.put(Constants.PROFILE_BLOOD_GROUP, bloodGroup);

                enableDisableElements(false);
                mFirestore.collection(Constants.COLLECTION_PROFILE)
                        .whereEqualTo(Constants.PROFILE_EMAIL, email)
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().size() > 0) {
                                enableDisableElements(true);
                                Utility.show(getView(), "User already exists");
                            } else {

                                mFirestore.collection(Constants.COLLECTION_PROFILE)
                                        .add(userObj)
                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isComplete()) {
                                                    Utility.show(getView(), "User registered successfully");
                                                    enableDisableElements(true);

                                                    session.setLastName(fname);
                                                    session.setFirstName(fname);
                                                    session.setEmail(email);

                                                    Intent dashboardIntent = new Intent(getContext(), MainActivity.class);
                                                    dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(dashboardIntent);

                                                }

                                            }
                                        });
                            }
                        }
                    }
                });
            }
        }


    }
}
