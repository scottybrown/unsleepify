package com.naur.unsleepify;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Random;

import static com.naur.unsleepify.MainActivity.DEFAULT_SAVED_ALARM;
import static com.naur.unsleepify.MainActivity.NOTIFICATION_CHANNEL_ID;
import static com.naur.unsleepify.MainActivity.SAVED_ALARM_IN_MILLIS_KEY;


public class ImpendingAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("onrec scott");
        showImpendingAlarmNotification(context);
    }

    private void showImpendingAlarmNotification(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        long existingAlarmLong = PreferenceManager.getDefaultSharedPreferences(context).getLong(SAVED_ALARM_IN_MILLIS_KEY, DEFAULT_SAVED_ALARM);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Unsleepification due at " + Utils.getHourColonMinute(existingAlarmLong))
                .setContentText("Tap to change")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat.from(context).notify(new Random().nextInt(), builder.build());
    }
}
