package com.naur.unsleepify;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Random;

import static com.naur.unsleepify.DateUtils.getTime;
import static com.naur.unsleepify.DateUtils.getTimeEightHoursFromNow;
import static com.naur.unsleepify.MainActivity.DEFAULT_SAVED_ALARM;
import static com.naur.unsleepify.MainActivity.SAVED_ALARM_IN_MILLIS_KEY;

public class MainActivity extends Activity {
    public static final String SAVED_ALARM_IN_MILLIS_KEY = "SAVED_ALARM_IN_MILLIS";
    public static final String SAVED_PLAYLIST_ID_KEY = "SAVED_PLAYLIST_ID";
    public static final String EXCLUDED_ARTISTS_KEY = "EXCLUDED_ARTISTS_KEY";
    public static final int DEFAULT_SAVED_ALARM = -1;
    public static final String NOTIFICATION_CHANNEL_ID = "1";

    private void createNotificationChannel() {
         int importance = NotificationManager.IMPORTANCE_HIGH;
         NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "pre_alarm_notification", importance);
         channel.setDescription("Notification before the alarm, giving user a chance to skip it if they're awake");
         getSystemService(NotificationManager.class).createNotificationChannel(channel);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        NotificationManagerCompat.from(this).cancelAll();
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
                stopService();
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
        alarmVolumeText.setText(percentageOf(alarmVolume, alarmMaxVolume) + "% Volume");
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
//        AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, DateUtils.getMillis(dateTime), AlarmManager.INTERVAL_DAY, setupAlarmBroadcastIntent());
//        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, DateUtils.getMillis(dateTime.minusMinutes(30)), AlarmManager.INTERVAL_DAY, setupImpendingAlarmNotificationBroadcastIntent());

//        Notification builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_bw)
//                .setContentTitle("Unsleepification due at some point...")
//                .setContentText("Tap to change")
//                .setPriority(Notification.PRIORITY_HIGH)
//                .setOngoing(true)
//                .build();
//        startForeground(1, notification);
        startService();


//        NotificationManagerCompat.from(this).notify(new Random().nextInt(), builder);

        // the idea is that running a foreground service with a notif means android won't play funny buggers with it
        // sounds shit. especially since my service literally does nothing. but we'll see if it stands the test of time.
//        it didn't work. but it looks like android respects that the foreground service is running.
//                trigger more of it from there. all of it
    }

    public void startService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Foreground Service Example in Android");
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        stopService(serviceIntent);
    }

    public void cancelBroadcastReceiver() {
        AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(setupAlarmBroadcastIntent());
        NotificationManagerCompat.from(this).cancelAll();
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

class ForegroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Foreground Service")
                .setContentText(input)

                .setSmallIcon(R.drawable.ic_bw)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);

        long existingAlarmLong = PreferenceManager.getDefaultSharedPreferences(this).getLong(SAVED_ALARM_IN_MILLIS_KEY, DEFAULT_SAVED_ALARM);
        LocalDateTime alarmDateTime = DateUtils.getLocalDateTime(LocalTime.ofSecondOfDay(existingAlarmLong));
        alarmDateTime = DateUtils.adjustToTomorrowIfBeforeOrEqualCurrentTime(alarmDateTime);

        AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, DateUtils.getMillis(alarmDateTime), AlarmManager.INTERVAL_DAY, setupAlarmBroadcastIntent());
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, DateUtils.getMillis(alarmDateTime.minusMinutes(30)), AlarmManager.INTERVAL_DAY, setupImpendingAlarmNotificationBroadcastIntent());
        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY;
    }

    public PendingIntent setupAlarmBroadcastIntent() {
        Intent intent = new Intent(this.getApplicationContext(), AlarmReceiver.class);
        return PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);
    }

    public PendingIntent setupImpendingAlarmNotificationBroadcastIntent() {
        Intent intent = new Intent(this.getApplicationContext(), ImpendingAlarmReceiver.class);
        return PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}


// seems perfect now. needs more testing.
// is setting the alarm in the service. hopefully that makes it long lived, as the service seems to live.