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
        Intent startActivityIntent = new Intent(context, SongPlayingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent startActivityPendingIntent = PendingIntent.getActivity(context, 0,
                startActivityIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_bw)
                        .setContentTitle("Unsleepifying now!")
                        .setContentText("Tap to open")
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setCategory(NotificationCompat.CATEGORY_ALARM)
                        .setAutoCancel(true)
                        .setFullScreenIntent(startActivityPendingIntent, true);

        NotificationManagerCompat.from(context).notify(new Random().nextInt(), notificationBuilder.build());
    }
}
// doesn't always play on right device?
// test when not plugged in
// make sure to cancel this notif when the activity opens
// todo add actions to notif?
// check out the notif when you hover the app's icon
// is due at notif getting set at the right time?