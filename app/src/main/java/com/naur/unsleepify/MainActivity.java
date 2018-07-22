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
import android.view.Window;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import static com.naur.unsleepify.DateUtils.getCalendar;

public class MainActivity extends Activity {
    public static final String SAVED_ALARM_IN_MILLIS = "SAVED_ALARM_IN_MILLIS";
    public static final int DEFULT_SAVED_ALARM_IN_MILLIS = -1;

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
                Calendar alarmTime = getCalendar(getHourPicker().getValue(), getMinutePicker().getValue());
                DateUtils.adjustToTomorrowIfBeforeOrEqualCurrentTime(alarmTime);

                setupRepeatingBroadcastReceiver(alarmTime);
                writePreference(SAVED_ALARM_IN_MILLIS, alarmTime.getTimeInMillis());

                toastify(DateUtils.getTimeDifferenceString(alarmTime));
                updateExistingAlarmText();

                updateAlarmVolumeText();// TODO better in some callback to keep it up to date when the user changes it. but simpler this way
            }
        });

        findViewById(R.id.CancelButton).setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                cancelBroadcastReceiver();
                writePreference(SAVED_ALARM_IN_MILLIS, DEFULT_SAVED_ALARM_IN_MILLIS);
                toastify("Alarm cancelled");
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

        Calendar defaultTime = DateUtils.calculateAlarmTimeOnly();
        hourPicker.setValue(defaultTime.get(Calendar.HOUR_OF_DAY));
        minutePicker.setValue(defaultTime.get(Calendar.MINUTE));
    }

    private void updateExistingAlarmText() {
        TextView existingAlarmText = findViewById(R.id.ExistingAlarm);
        long existingAlarmInMillis = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong(SAVED_ALARM_IN_MILLIS, DEFULT_SAVED_ALARM_IN_MILLIS);
        if (existingAlarmInMillis != DEFULT_SAVED_ALARM_IN_MILLIS) {
            Calendar existingAlarm = getCalendar(existingAlarmInMillis);
            existingAlarmText.setText(existingAlarm.get(Calendar.HOUR_OF_DAY) + ":" + existingAlarm.get(Calendar.MINUTE));
        } else {
            existingAlarmText.setText("No alarm set...");
        }
    }

    private void updateAlarmVolumeText() {
        TextView alarmVolumeText = findViewById(R.id.AlarmVolume);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int alarmMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        alarmVolumeText.setText("Alarm volume: " +percentageOf(alarmVolume,alarmMaxVolume)+"%");
    }
private int percentageOf(int number, int max){
    return (number * 100) / max;
}
    public PendingIntent setupBroadcastIntent() {
        AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this.getApplicationContext(), AlarmReceiver.class);
        return PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);
    }

    public void setupRepeatingBroadcastReceiver(Calendar alarmTime) {
        AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, setupBroadcastIntent());
    }

    public void cancelBroadcastReceiver() {
        AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(setupBroadcastIntent());
    }

    private void toastify(String text) {
        Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
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