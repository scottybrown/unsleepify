package com.naur.unsleepify;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.util.Calendar;

// doesn't like having more than 1 alarm. seems to overwrite.
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final NumberPicker hourPicker=findViewById(R.id.HourPicker);
        hourPicker.setMinValue(1);
        hourPicker.setMaxValue(24);

        final NumberPicker minutePicker=findViewById(R.id.MinutePicker);
        minutePicker.setMinValue(1);
        minutePicker.setMaxValue(60);

        Button submitButton=findViewById(R.id.SubmitButton);
        submitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setupRepeatingBroadcastReceiver(getTimeInMillis(hourPicker.getValue(),minutePicker.getValue()));
                toastify("Alarm configured for "+hourPicker.getValue()+":"+minutePicker.getValue());
            }
        });

    }

    public long getTimeInMillis(long hour, long minute) {
        Calendar alarmStartTime = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY, (int)hour);
        alarmStartTime.set(Calendar.MINUTE, (int)minute);
        alarmStartTime.set(Calendar.SECOND, 0);

        if (now.after(alarmStartTime)) {
            alarmStartTime.add(Calendar.DATE, 1);
        }

        return alarmStartTime.getTimeInMillis();
    }

    public void setupRepeatingBroadcastReceiver(long alarmTimeInMillis) {
        AlarmManager alarmManager = (AlarmManager) this.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this.getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent, 0);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void toastify(String text) {
        Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}