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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.yashbhatt.bloodbank.BloodBank;
import com.yashbhatt.bloodbank.MainActivity;
import com.yashbhatt.bloodbank.R;
import com.yashbhatt.bloodbank.RegistrationActivity;
import com.yashbhatt.bloodbank.global.Constants;
import com.yashbhatt.bloodbank.global.SessionManager;
import com.yashbhatt.bloodbank.global.Utility;

import java.util.HashMap;

import static com.yashbhatt.bloodbank.global.Constants.COLLECTION_PROFILE;
import static com.yashbhatt.bloodbank.global.Constants.PROFILE_EMAIL;
import static com.yashbhatt.bloodbank.global.Constants.PROFILE_PASS;

public class SignInFragment extends Fragment implements View.OnClickListener {
    public static final String TAG = "SignInFragment";

    private View signInView;
    private FirebaseFirestore mFireStore;
    private SessionManager session;

    private EditText editEmail, editPass;
    private Button btnSignIn;
    private ProgressBar progressSignin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (signInView == null) {
            signInView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        }

        mFireStore = ((BloodBank) getActivity().getApplication()).getFirestore();
        session = ((BloodBank) getActivity().getApplication()).getSession();

        editEmail = signInView.findViewById(R.id.edit_email);
        editPass = signInView.findViewById(R.id.edit_pass);
        btnSignIn = signInView.findViewById(R.id.btn_login);
        btnSignIn.setOnClickListener(this);
        progressSignin = signInView.findViewById(R.id.progress_signin);

        signInView.findViewById(R.id.text_btn_create_account).setOnClickListener(this);
        return signInView;
    }

    private void enableDisable(boolean flag) {
        if (flag) {
            btnSignIn.setVisibility(View.VISIBLE);
            progressSignin.setVisibility(View.GONE);
        } else {
            btnSignIn.setVisibility(View.GONE);
            progressSignin.setVisibility(View.VISIBLE);
        }
        editPass.setEnabled(flag);
        editEmail.setEnabled(flag);
        btnSignIn.setEnabled(flag);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.text_btn_create_account) {
            ((RegistrationActivity) getActivity())
                    .openFragment(new SignUpFragment(), SignUpFragment.TAG);
        } else if (v.getId() == R.id.btn_login) {
            login(editEmail.getText().toString().trim(), editPass.getText().toString().trim());
        }

    }

    private void login(final String email, final String pass) {
        enableDisable(false);
        mFireStore.collection(COLLECTION_PROFILE)
                .whereEqualTo(PROFILE_EMAIL, email)
                .whereEqualTo(PROFILE_PASS, pass)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isComplete()) {
                            if (task.getResult().getDocuments().size() >= 1) {
                                enableDisable(true);

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
