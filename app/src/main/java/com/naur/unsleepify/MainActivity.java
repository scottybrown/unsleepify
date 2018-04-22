package com.naur.unsleepify;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import static com.naur.unsleepify.DateUtils.getCalendar;

// button to cancel alarm
// notif before and during alarm
// todo some error handling, would like to know why it failed since i expect it to
// lock screen notif
// show art. pref for band and song off album.
// configure playlist in gui
// not spinners. pop up an input box
// report how far away you set an alarm
//√ show when the one alarm is set
//√ if it gets the last song in the playlist it goes to the start but doesn't play. can test this setting index manually
//√ todo sets volume
//√ todo stops if minimized, but runs when locked
//√ runs at a time
//√ can get song name. though can always shazam it kek
//√ configure the time or times using gui
//√ make time default to 8h from now
//√ todo get playlist, play songs
// not sure it actually repeats every day. should test. without resetting it
public class MainActivity extends Activity {
    public static final String SAVED_ALARM_IN_MILLIS = "SAVED_ALARM_IN_MILLIS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeNumberPickers();
        updateAlarmVolumeText();
        updateExistingAlarmText();

        Button submitButton = findViewById(R.id.SubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar alarmTime = getCalendar(getHourPicker().getValue(), getMinutePicker().getValue());
                // TODO test this too
                boolean same =
                        Calendar.getInstance().get(Calendar.HOUR_OF_DAY) == alarmTime.get(Calendar.HOUR_OF_DAY) && Calendar.getInstance().get(Calendar.MINUTE) == alarmTime.get(Calendar.MINUTE);
                if (Calendar.getInstance().after(alarmTime) || same) {
                    alarmTime.add(Calendar.DATE, 1);
                }

                setupRepeatingBroadcastReceiver(alarmTime);
                writePreference(SAVED_ALARM_IN_MILLIS, alarmTime.getTimeInMillis());

                toastify(DateUtils.getTimeDifferenceString(alarmTime));
                updateExistingAlarmText();

                updateAlarmVolumeText();// TODO better in some callback to keep it up to date when the user changes it. but simpler this way
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
        long defaultValue = -1l;
        long existingAlarmInMillis = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong(SAVED_ALARM_IN_MILLIS, defaultValue);
        if (existingAlarmInMillis != defaultValue) {
            Calendar existingAlarm = getCalendar(existingAlarmInMillis);
            existingAlarmText.setText(existingAlarm.get(Calendar.HOUR_OF_DAY) + ":" + existingAlarm.get(Calendar.MINUTE));
        }
    }

    private void updateAlarmVolumeText() {
        TextView alarmVolumeText = findViewById(R.id.AlarmVolume);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        int alarmMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        alarmVolumeText.setText("Alarm volume: " + alarmVolume + "/" + alarmMaxVolume);
    }

    public void setupRepeatingBroadcastReceiver(Calendar alarmTime) {
        AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this.getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
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