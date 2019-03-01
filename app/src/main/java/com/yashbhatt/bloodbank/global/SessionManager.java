/*
 * Confidential and Proprietary
 * Copyright © 2016 FitnessCubed, Inc.
 * All Rights Reserved.
 */

package com.yashbhatt.bloodbank.global;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * This class hold all stored value that's required anywhere.
 * Created by sanjay on 15/10/15.
 */
public class SessionManager {

    private SharedPreferences preferences;
    private Context context;

    public SessionManager(Context context) {
        preferences = context.getSharedPreferences("Cubii", Context.MODE_PRIVATE);
        this.context = context;
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------
    public void setFirstName(String firstName) {
        preferences.edit().putString("first_name", firstName).apply();
    }

    public String getFirstName() {
        return preferences.getString("first_name", "");
    }

    public void setLastName(String lastName) {
        preferences.edit().putString("last_name", lastName).apply();
    }

    public String getLastName() {
        return preferences.getString("last_name", "");
    }

    public void setEmail(String email) {
        preferences.edit().putString("email", email).apply();
    }

    public String getEmail() {
        return preferences.getString("email", null);
    }

    public void clear() {
        preferences.edit().clear().apply();
    }

}
