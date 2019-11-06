package com.naur.unsleepify;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.naur.unsleepify.DateUtils.getTime;
import static com.naur.unsleepify.DateUtils.getTimeEightHoursFromNow;

public class MainActivity extends Activity {
    public static final String SAVED_ALARM_IN_MILLIS_KEY = "SAVED_ALARM_IN_MILLIS";
    public static final String SAVED_PLAYLIST_ID_KEY = "SAVED_PLAYLIST_ID";
    public static final String EXCLUDED_ARTISTS_KEY = "EXCLUDED_ARTISTS_KEY";
    public static final int DEFAULT_SAVED_ALARM = -1;
    public static final String NOTIFICATION_CHANNEL_ID = "1";

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "pre_alarm_notification", importance);
            channel.setDescription("Notification before the alarm, giving user a chance to skip it if they're awake");
            getSystemService(NotificationManager.class).createNotificationChannel(channel);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        createNotificationChannel();
        initializeNumberPickers();
        updateAlarmVolumeText();
        updateExistingAlarmText();
        updatePlaylistIDText();
        updateExcludedArtistsText();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        findViewById(R.id.SubmitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocalTime alarmTime = getTime(getHourPicker().getValue(), getMinutePicker().getValue());
                writePreference(SAVED_ALARM_IN_MILLIS_KEY, alarmTime.toSecondOfDay());
                writePreference(SAVED_PLAYLIST_ID_KEY, ((EditText) findViewById(R.id.PlaylistId)).getText().toString());
                writePreference(EXCLUDED_ARTISTS_KEY, ((EditText) findViewById(R.id.ExcludedArtists)).getText().toString());

                updateExistingAlarmText();

                LocalDateTime alarmDateTime = DateUtils.getLocalDateTime(alarmTime);
                alarmDateTime = DateUtils.adjustToTomorrowIfBeforeOrEqualCurrentTime(alarmDateTime);
                setupRepeatingBroadcastReceiver(alarmDateTime);
                Utils.toastify(DateUtils.getTimeDifferenceString(alarmDateTime), getApplicationContext());

                // TODO better in some callback to keep it up to date when the user changes it. but simpler this way
                updateAlarmVolumeText();
            }
        });

        findViewById(R.id.CancelButton).setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                cancelBroadcastReceiver();
                writePreference(SAVED_ALARM_IN_MILLIS_KEY, DEFAULT_SAVED_ALARM);
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
        long existingAlarmLong = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong(SAVED_ALARM_IN_MILLIS_KEY, DEFAULT_SAVED_ALARM);
        if (existingAlarmLong != DEFAULT_SAVED_ALARM) {
            existingAlarmText.setText(Utils.getHourColonMinute(existingAlarmLong));
        } else {
            existingAlarmText.setText("- -:- -");
        }
    }

    private void updatePlaylistIDText() {
        TextView playlistIdText = findViewById(R.id.PlaylistId);
        String existingPlaylistId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(SAVED_PLAYLIST_ID_KEY, getString(R.string.default_playlist_id));
        playlistIdText.setText(existingPlaylistId);
    }

    private void updateExcludedArtistsText() {
        TextView excludedArtistsText = findViewById(R.id.ExcludedArtists);
        String existingExcludedArtists = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(EXCLUDED_ARTISTS_KEY, "");
        excludedArtistsText.setText(existingExcludedArtists);
    }

    private void updateAlarmVolumeText() {
        TextView alarmVolumeText = findViewById(R.id.AlarmVolume);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int alarmMaxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        alarmVolumeText.setText(+percentageOf(alarmVolume, alarmMaxVolume) + "% Volume");
    }

    private int percentageOf(int number, int max) {
        return (number * 100) / max;
    }

    public PendingIntent setupAlarmBroadcastIntent() {
        Intent intent = new Intent(this.getApplicationContext(), AlarmReceiver.class);
        return PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);
    }

    public PendingIntent setupImpendingAlarmNotificationBroadcastIntent() {
        Intent intent = new Intent(this.getApplicationContext(), ImpendingAlarmReceiver.class);
        return PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);
    }

    public void setupRepeatingBroadcastReceiver(LocalDateTime dateTime) {
        AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, DateUtils.getMillis(dateTime), AlarmManager.INTERVAL_DAY, setupAlarmBroadcastIntent());
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, DateUtils.getMillis(dateTime.minusMinutes(30)), AlarmManager.INTERVAL_DAY, setupImpendingAlarmNotificationBroadcastIntent());
    }

    public void cancelBroadcastReceiver() {
        AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(setupAlarmBroadcastIntent());
        NotificationManagerCompat.from(this ).cancelAll();
    }

    private void writePreference(String preferenceKey, long preference) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putLong(preferenceKey, preference);
        editor.apply();
    }

    private void writePreference(String preferenceKey, String preference) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
        editor.putString(preferenceKey, preference);
        editor.apply();
    }

    private NumberPicker getHourPicker() {
        return findViewById(R.id.HourPicker);
    }

    private NumberPicker getMinutePicker() {
        return findViewById(R.id.MinutePicker);
    }
}