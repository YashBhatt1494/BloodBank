package com.yashbhatt.bloodbank.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yashbhatt.bloodbank.R;
import com.yashbhatt.bloodbank.models.Donor;

import java.util.ArrayList;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.ViewHolder> {

    private ArrayList<Donor> arrDonors;

    public DonorAdapter(ArrayList<Donor> arrDonors) {
        this.arrDonors = arrDonors;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_donor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        try{
           Donor donor = arrDonors.get(position);

            holder.textName.setText(String.valueOf(donor.getName()));
            holder.textEmail.setText(String.valueOf(donor.getEmail()));
            holder.textBloodGroup.setText(String.valueOf(donor.getBgroup()));

        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if (arrDonors != null) {
            return arrDonors.size();
        }
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textEmail, textBloodGroup;

        public ViewHolder(View itemView) {
            super(itemView);

            textBloodGroup = itemView.findViewById(R.id.text_blood_group);
            textName = itemView.findViewById(R.id.text_name);
            textEmail = itemView.findViewById(R.id.text_email);
        }
    }
}
