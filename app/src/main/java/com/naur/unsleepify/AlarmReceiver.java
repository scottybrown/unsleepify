package com.naur.unsleepify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationManagerCompat;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManagerCompat.from(context).cancelAll();
        Intent startActivityIntent = new Intent();
        startActivityIntent.setClassName(MainActivity.class.getPackage().getName(), SongPlayingActivity.class.getName());
        startActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startActivityIntent);
    }
}
