package com.yashbhatt.bloodbank.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.yashbhatt.bloodbank.BloodBank;
import com.yashbhatt.bloodbank.R;
import com.yashbhatt.bloodbank.adapters.DonorAdapter;
import com.yashbhatt.bloodbank.global.Constants;
import com.yashbhatt.bloodbank.global.Utility;
import com.yashbhatt.bloodbank.models.Donor;

import java.util.ArrayList;
import java.util.List;

public class FindDonorFragment extends Fragment implements Spinner.OnItemSelectedListener, SwipeRefreshLayout.OnRefreshListener {
    public static final String TAG = "FindDonorFragment";

    private View findView;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Spinner bgroupSpinner;
    private ProgressBar progressBar;

    private FirebaseFirestore mFireStore;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (findView == null) {
            findView = inflater.inflate(R.layout.fragment_find_donor, container, false);
        }

        recyclerView = findView.findViewById(R.id.recycler_find_donor);
        bgroupSpinner = findView.findViewById(R.id.spinner_blood_group);
        bgroupSpinner.setOnItemSelectedListener(this);
//        bgroupSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//        });
        swipeRefreshLayout = findView.findViewById(R.id.swipe_donors);
        swipeRefreshLayout.setOnRefreshListener(this);
        progressBar = findView.findViewById(R.id.progress_find_donor);

        mFireStore = ((BloodBank) getActivity().getApplication()).getFirestore();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        return findView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getAllDonors();
    }

    /**
     * function to get searched bgroup donors
     *
     * @param searchGroup
     */
    private void getDonors(String searchGroup) {
        progressBar.setVisibility(View.VISIBLE);

        Query query = mFireStore.collection(Constants.COLLECTION_DONORS)
                .whereEqualTo(Constants.PROFILE_BLOOD_GROUP, searchGroup);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult() != null) {
                        if (!task.getResult().isEmpty()) {
                            bindDonors(task.getResult().getDocuments());
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Utility.show(getView(), "No data found");
                        }
                    }

                }

            }
        });


    }

    private void bindDonors(List<DocumentSnapshot> documentSnapshots) {

        try {
            ArrayList<Donor> arrayDonors = new ArrayList<>();
            for (DocumentSnapshot document : documentSnapshots) {
                String name = String.valueOf(document.get(Constants.PROFILE_FNAME));
                String email = String.valueOf(document.get(Constants.PROFILE_EMAIL));
                String bgroup = String.valueOf(document.get(Constants.PROFILE_BLOOD_GROUP));

                final Donor donor = new Donor();

                donor.setBgroup(bgroup);
                donor.setName(name);
                donor.setEmail(email);

                arrayDonors.add(donor);

            }

            final DonorAdapter donorAdapter = new DonorAdapter(arrayDonors);
            recyclerView.setAdapter(donorAdapter);
            progressBar.setVisibility(View.GONE);


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getAllDonors() {
        progressBar.setVisibility(View.VISIBLE);
        mFireStore.collection(Constants.COLLECTION_DONORS)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    if (task.getResult() != null) {
                        if (!task.getResult().isEmpty()) {
                            bindDonors(task.getResult().getDocuments());
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Utility.show(getView(), "No data found");
                        }
                    }

                }

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String bgroup = bgroupSpinner.getSelectedItem().toString();
        Log.e(TAG, bgroup + " selected");

        getDonors(bgroup);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        getAllDonors();
    }
}
