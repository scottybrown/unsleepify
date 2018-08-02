package com.naur.unsleepify;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.naur.unsleepify.DateUtils.getTime;
import static com.naur.unsleepify.DateUtils.getTimeEightHoursFromNow;

public class MainActivity extends Activity {
    public static final String SAVED_ALARM_IN_MILLIS = "SAVED_ALARM_IN_MILLIS";
    public static final int DEFULT_SAVED_ALARM = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeNumberPickers();
        updateAlarmVolumeText();
        updateExistingAlarmText();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        findViewById(R.id.SubmitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalTime alarmTime = getTime(getHourPicker().getValue(), getMinutePicker().getValue());
                writePreference(SAVED_ALARM_IN_MILLIS, alarmTime.toSecondOfDay());
                updateExistingAlarmText();

                LocalDateTime alarmDateTime = DateUtils.getLocalDateTime(alarmTime);
                alarmDateTime = DateUtils.adjustToTomorrowIfBeforeOrEqualCurrentTime(alarmDateTime);
                setupRepeatingBroadcastReceiver(alarmDateTime);
                Utils.toastify(DateUtils.getTimeDifferenceString(alarmDateTime), getApplicationContext());

                updateAlarmVolumeText();// TODO better in some callback to keep it up to date when the user changes it. but simpler this way
            }
        });

        findViewById(R.id.CancelButton).setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                cancelBroadcastReceiver();
                writePreference(SAVED_ALARM_IN_MILLIS, DEFULT_SAVED_ALARM);
                Utils.toastify("Alarm cancelled", getApplicationContext());
                updateExistingAlarmText();
            }
        });
    }

    private void initializeNumberPickers() {
        final NumberPicker hourPicker = findViewById(R.id.HourPicker);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);

        final NumberPicker minutePicker = findViewById(R.id.MinutePicker);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);

        LocalTime defaultTime = getTimeEightHoursFromNow();
        hourPicker.setValue(defaultTime.getHour());
        minutePicker.setValue(defaultTime.getMinute());
    }

    private void updateExistingAlarmText() {
        TextView existingAlarmText = findViewById(R.id.ExistingAlarm);
        long existingAlarmLong = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong(SAVED_ALARM_IN_MILLIS, DEFULT_SAVED_ALARM);
        if (existingAlarmLong != DEFULT_SAVED_ALARM) {
            LocalTime existingAlarm = getTime(existingAlarmLong);
            String hour = Utils.leftPad(existingAlarm.getHour(), 0, 2);
            String minute = Utils.leftPad(existingAlarm.getMinute(), 0, 2);
            existingAlarmText.setText(hour + ":" + minute);
            // todo resource string with placeholders
        } else {
            existingAlarmText.setText("- -:- -");
        }
    }

    private void updateAlarmVolumeText() {
        TextView alarmVolumeText = findViewById(R.id.AlarmVolume);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int alarmMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        alarmVolumeText.setText(+ percentageOf(alarmVolume, alarmMaxVolume) + "% Volume");
    }

    private int percentageOf(int number, int max) {
        return (number * 100) / max;
    }

    public PendingIntent setupBroadcastIntent() {
        Intent intent = new Intent(this.getApplicationContext(), AlarmReceiver.class);
        return PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);
    }

    public void setupRepeatingBroadcastReceiver(LocalDateTime dateTime) {
        AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, DateUtils.getMillis(dateTime), AlarmManager.INTERVAL_DAY, setupBroadcastIntent());
    }

    public void cancelBroadcastReceiver() {
        AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(setupBroadcastIntent());
    }

    private void writePreference(String preferenceKey, long preference) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putLong(preferenceKey, preference);
        editor.apply();
    }

    private NumberPicker getHourPicker() {
        return findViewById(R.id.HourPicker);
    }

    private NumberPicker getMinutePicker() {
        return findViewById(R.id.MinutePicker);
    }
}