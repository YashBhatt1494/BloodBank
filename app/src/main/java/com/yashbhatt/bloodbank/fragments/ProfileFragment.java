package com.yashbhatt.bloodbank.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.yashbhatt.bloodbank.BloodBank;
import com.yashbhatt.bloodbank.R;
import com.yashbhatt.bloodbank.global.Constants;
import com.yashbhatt.bloodbank.global.SessionManager;
import com.yashbhatt.bloodbank.global.Utility;

public class ProfileFragment extends Fragment {
    public static final String TAG = "ProfileFragment";
    private View profileView;
    private TextView textName, textBloodGroup, textAge, textTotal;
    private SessionManager session;
    private FirebaseFirestore firestore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (profileView == null) {
            profileView = inflater.inflate(R.layout.fragment_profile, container, false);
        }

        session = ((BloodBank) getActivity().getApplication()).getSession();
        firestore = ((BloodBank) getActivity().getApplication()).getFirestore();

        textAge = profileView.findViewById(R.id.text_profile_age);
        textName = profileView.findViewById(R.id.text_name);
        textBloodGroup = profileView.findViewById(R.id.text_blood_group);
        textTotal = profileView.findViewById(R.id.text_profile_donation_count);


        return profileView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fillData();
    }

    private void fillData() {
        textTotal.setText("0");

        firestore.collection(Constants.COLLECTION_DONORS)
                .whereEqualTo(Constants.PROFILE_EMAIL, session.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int total = task.getResult().getDocuments().size();
                            textTotal.setText(String.valueOf(total));
                        }
                    }
                });


    }


}
