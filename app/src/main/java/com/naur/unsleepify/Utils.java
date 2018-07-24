package com.naur.unsleepify;

import android.app.Activity;

import android.content.Context;
import android.widget.Toast;

public class Utils {
    public static void toastify(String text, Context applicationContext) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show();
    }
}