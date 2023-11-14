package com.naur.unsleepify;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Random;

import static com.naur.unsleepify.MainActivity.NOTIFICATION_CHANNEL_ID;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("scott alarmreceiver start");
        NotificationManagerCompat.from(context).cancelAll();

        Intent startActivityIntent = new Intent(context, SongPlayingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0,
                startActivityIntent, 0);

        Intent mainActivityIntent = new Intent(context, MainActivity.class);
        PendingIntent mainActivityPendingIntent = PendingIntent.getActivity(context, 0,
                mainActivityIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_bw)
                        .setContentTitle("Unsleepify is alarming!")
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setAutoCancel(true)
                        .setFullScreenIntent(startActivityPendingIntent, true)
                        .addAction(R.drawable.ic_bw, "Dismiss", mainActivityPendingIntent);

        NotificationManagerCompat.from(context).notify(new Random().nextInt(), notificationBuilder.build());
        System.out.println("scott alarmreceiver end");

    }
}