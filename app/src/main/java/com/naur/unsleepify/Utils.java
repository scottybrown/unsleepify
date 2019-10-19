package com.naur.unsleepify;

import android.content.Context;
import android.widget.Toast;

import java.time.LocalTime;

import static com.naur.unsleepify.DateUtils.getTime;

public class Utils {
    public static void toastify(String text, Context applicationContext) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show();
    }

    public static String leftPad(int number, int paddingDigit, int width) {
        return String.format("%" + paddingDigit + width + "d", number);
    }

    public static String getHourColonMinute(long existingAlarmLong) {
        LocalTime existingAlarm = getTime(existingAlarmLong);
        String hour = Utils.leftPad(existingAlarm.getHour(), 0, 2);
        String minute = Utils.leftPad(existingAlarm.getMinute(), 0, 2);
        return hour + ":" + minute;
    }

}