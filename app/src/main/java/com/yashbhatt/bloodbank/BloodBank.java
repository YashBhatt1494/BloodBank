package com.yashbhatt.bloodbank;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.firestore.FirebaseFirestore;
import com.yashbhatt.bloodbank.global.SessionManager;

public class BloodBank extends MultiDexApplication {

    private FirebaseFirestore firestore;
    private SessionManager session;

    @Override
    public void onCreate() {
        super.onCreate();

        firestore = FirebaseFirestore.getInstance();
        session = new SessionManager(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this); // this is the key code
    }

    public FirebaseFirestore getFirestore() {
        return firestore;
    }

    public SessionManager getSession() {
        return session;
    }


}
