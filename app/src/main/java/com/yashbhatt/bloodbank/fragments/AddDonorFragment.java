package com.yashbhatt.bloodbank.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.yashbhatt.bloodbank.BloodBank;
import com.yashbhatt.bloodbank.MainActivity;
import com.yashbhatt.bloodbank.R;
import com.yashbhatt.bloodbank.global.Constants;
import com.yashbhatt.bloodbank.global.SessionManager;
import com.yashbhatt.bloodbank.global.Utility;

import java.util.HashMap;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class AddDonorFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "AddDonorFragment";
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private View donorView;
    private Spinner spinnerBlood;
    private TextView textAddress;
    private Button btnDOnate;
    private Place selectedPlace;
    private ProgressBar progress;


    private FirebaseFirestore mFirestore;
    private SessionManager session;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (donorView == null) {
            donorView = inflater.inflate(R.layout.fragment_add_donor, container, false);


            mFirestore = ((BloodBank) getActivity().getApplication()).getFirestore();
            session = ((BloodBank) getActivity().getApplication()).getSession();

            textAddress = donorView.findViewById(R.id.text_select_address);
            spinnerBlood = donorView.findViewById(R.id.spinner_blood_group);
            progress = donorView.findViewById(R.id.progress_add_donor);

            btnDOnate = donorView.findViewById(R.id.btn_donate);
            btnDOnate.setOnClickListener(this);

            donorView.findViewById(R.id.image_back).setOnClickListener(this);
        }

        textAddress.setOnClickListener(this);

        return donorView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.text_select_address) {
            openAutoComplete();
            return;
        } else if (view.getId() == R.id.btn_donate) {
            donate();
        } else if (view.getId() == R.id.image_back) {
            getActivity().onBackPressed();
        }
    }

    private void donate() {

        if (selectedPlace == null) {
            Toast.makeText(getContext(), "Please select address", Toast.LENGTH_LONG).show();
            return;
        }

        String bgroup = spinnerBlood.getSelectedItem().toString();
        String address = selectedPlace.getName().toString();
        final String fname = session.getFirstName();
        String lname = session.getLastName();
        final String email = session.getEmail();

        if (getActivity() != null) {
            HashMap<String, Object> userObj = new HashMap<>();
            userObj.put(Constants.PROFILE_FNAME, fname);
            userObj.put(Constants.PROFILE_LNAME, lname);
            userObj.put(Constants.PROFILE_EMAIL, email);
            userObj.put(Constants.PROFILE_BLOOD_GROUP, bgroup);
            userObj.put(Constants.LAT, selectedPlace.getLatLng().latitude);
            userObj.put(Constants.LONGT, selectedPlace.getLatLng().longitude);
            btnDOnate.setVisibility(View.GONE);
            progress.setVisibility(View.VISIBLE);
            mFirestore.collection(Constants.COLLECTION_DONORS)
                    .add(userObj)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isComplete()) {

                                btnDOnate.setVisibility(View.VISIBLE);
                                progress.setVisibility(View.GONE);

                                Intent dashboardIntent = new Intent(getContext(), MainActivity.class);
                                dashboardIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(dashboardIntent);

                            }

                        }
                    });
        }


    }

    private void openAutoComplete() {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(getActivity());
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                Log.i(TAG, "Place: " + place.getName());
                this.selectedPlace = place;
                textAddress.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

}
