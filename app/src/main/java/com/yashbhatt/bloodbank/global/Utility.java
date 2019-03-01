package com.yashbhatt.bloodbank.global;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

public class Utility {

    public static void show(View view, String message) {
        if (view != null && message != null && !message.isEmpty()) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
        }
    }
}
