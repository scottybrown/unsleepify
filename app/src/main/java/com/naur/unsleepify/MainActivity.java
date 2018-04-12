package com.naur.unsleepify;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

// back from main activity opens the other activity i think
// show when the one alarm is set
// should store a list of alarms rather than just sending broadcast receivers out into the ether. then show that list.
// shuffle songs playing
//√ todo get playlist, play songs
// todo some error handling, would like to know why it failed since i expect it to
// todo sets volume
// todo stops if minimized, but runs when locked
// runs at a time
// can get song name. though can always shazam it kek
// lock screen notif
// show art. pref for band and song off album.
// configure the time or times using gui
// configure playlist in gui
// not spinners. pop up an input box
// make time default to 8h from now
// report how far away you set an alarm
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
                if (Calendar.getInstance().after(alarmTime)) {
                    alarmTime.add(Calendar.DATE, 1);
                }

                setupRepeatingBroadcastReceiver(alarmTime);
                writePreference(SAVED_ALARM_IN_MILLIS, alarmTime.getTimeInMillis());

                showToastOfTimeDifference(alarmTime); // TODO
                toastify("Alarm set for: "+alarmTime.get(Calendar.HOUR_OF_DAY)+":"+alarmTime.get(Calendar.MINUTE));
                updateExistingAlarmText();

                updateAlarmVolumeText();// TODO better in some callback but simpler this way
            }
        });
    }

    private void showToastOfTimeDifference(Calendar alarmTime){
        long timeDifferenceInMillis=alarmTime.getTimeInMillis()-new Date().getTime();
        long timeDifferenceInMins=timeDifferenceInMillis/(60*1000);
        long timeDifferenceInHours=timeDifferenceInMillis/(60*1000*60);
//        long remainder=timeDifferenceInMins%timeDifferenceInHours;
        long remainder2=timeDifferenceInMins-timeDifferenceInHours*60;
        // the time and date it's using here isn't right.
        System.out.println("SCOTT"+timeDifferenceInHours+1+":"+remainder2);
    }

    private void initializeNumberPickers() {
        final NumberPicker hourPicker = findViewById(R.id.HourPicker);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(23);

        final NumberPicker minutePicker = findViewById(R.id.MinutePicker);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(59);

        Calendar defaultTime = calculateAlarmTime();
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

    public static Calendar getEightHoursFromNow() {
        long eightHoursFromNow = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + 8;

        if (eightHoursFromNow > 23) {
            eightHoursFromNow = eightHoursFromNow - 24;
        }

        return getCalendar((int)eightHoursFromNow,Calendar.getInstance().get(Calendar.MINUTE));
    }

    public static Calendar calculateAlarmTime() {
        Calendar time = getEightHoursFromNow();
        time.set(Calendar.SECOND, 0);
        return time;
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

    public static Calendar getCalendar(int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        return cal;
    }

    private Calendar getCalendar(long existingAlarmInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(existingAlarmInMillis);
        return calendar;
    }

    private NumberPicker getHourPicker() {
        return findViewById(R.id.HourPicker);
    }

    private NumberPicker getMinutePicker() {
        return findViewById(R.id.MinutePicker);
    }
}