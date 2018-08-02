package com.naur.unsleepify;

import android.app.Activity;

import android.content.Context;
import android.widget.Toast;

public class Utils {
    public static void toastify(String text, Context applicationContext) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show();
    }

    public static String leftPad(int number, int paddingDigit, int width) {
        return String.format("%" + paddingDigit + width + "d", number);
    }
}