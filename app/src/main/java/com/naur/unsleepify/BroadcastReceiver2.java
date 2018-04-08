package com.naur.unsleepify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;

import static com.naur.unsleepify.MainActivity.mPlayer;

public class  BroadcastReceiver2 extends BroadcastReceiver
        {
            final Player.OperationCallback cb = new Player.OperationCallback() {
                @Override
                public void onSuccess() {
                    updateTextToSongInformation();
                }

                @Override
                public void onError(Error error) {

                }
            };

            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("Receiver", "Fucking broadcoasting yo");
//MainActivity.view.setText("fuckyeah");
                Intent i = new Intent();
                i.setClassName("com.naur.unsleepify", "com.naur.unsleepify.MainActivity"); // todo don't hard code
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

                //mPlayer.playUri(cb, "spotify:playlist:3pBnQakqa3Cd13p4qQP5Rn", 0, 0);
            }

            private void updateTextToSongInformation(){
                if(mPlayer.getMetadata().currentTrack!=null) {
                    MainActivity.view.setText(mPlayer.getMetadata().currentTrack.artistName + " - " + mPlayer.getMetadata().currentTrack.name);
                }};
        }
