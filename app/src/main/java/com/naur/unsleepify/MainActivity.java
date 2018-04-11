package com.naur.unsleepify;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

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
    public static final String SAVED_ALARM_IN_MILLIS="SAVED_ALARM_IN_MILLIS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final NumberPicker hourPicker = findViewById(R.id.HourPicker);
        hourPicker.setMinValue(1);
        hourPicker.setMaxValue(24);

        final NumberPicker minutePicker = findViewById(R.id.MinutePicker);
        minutePicker.setMinValue(1);
        minutePicker.setMaxValue(60);

        TextView existingAlarm = findViewById(R.id.ExistingAlarm);
        long defaultValue=-1l;
        long existingAlarmInMillis=PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getLong(SAVED_ALARM_IN_MILLIS, defaultValue);
        if(existingAlarmInMillis!=-1l) {
            Calendar existingAlarmCalendar=Calendar.getInstance();
            existingAlarmCalendar.setTimeInMillis(existingAlarmInMillis);
            long existingAlarmHour = existingAlarmCalendar.get(Calendar.HOUR_OF_DAY);
            long existingAlarmMinute = existingAlarmCalendar.get(Calendar.MINUTE);
            existingAlarm.setText(existingAlarmHour + ":" + existingAlarmMinute);
//            hourPicker.setValue((int)existingAlarmHour);
//            minutePicker.setValue((int)existingAlarmMinute);
//        }else{
        }
        Calendar cal=Calendar.getInstance();
            Calendar defaultTime=calculateAlarmTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
            hourPicker.setValue(defaultTime.get(Calendar.HOUR_OF_DAY));
            minutePicker.setValue(defaultTime.get(Calendar.MINUTE));


        Button submitButton = findViewById(R.id.SubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {
// todo clean up all cal stuff
            // should always default to current date.
            @Override
            public void onClick(View v) {
                Calendar cal=Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY,hourPicker.getValue());
                cal.set(Calendar.MINUTE, minutePicker.getValue());
                setupRepeatingBroadcastReceiver(cal);

                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putLong(SAVED_ALARM_IN_MILLIS,cal.getTimeInMillis());
                editor.apply();
            }
        });

    }

    public static Calendar getEightHoursFromNow(long hour, long minutes) {
        long eightHoursFromNow = hour + 8;
        Calendar now = Calendar.getInstance();

        if (eightHoursFromNow > 23) {
            eightHoursFromNow = eightHoursFromNow - 24;
            now.add(Calendar.DAY_OF_MONTH, 1);
        }
        now.set(Calendar.HOUR_OF_DAY, (int) eightHoursFromNow);
        return now;
    }

    public static Calendar calculateAlarmTime(long hour, long minute) {
        Calendar time = getEightHoursFromNow(hour, minute);
        time.set(Calendar.SECOND, 0);

        if (Calendar.getInstance().after(time)) {
            time.add(Calendar.DATE, 1);
        }

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
}