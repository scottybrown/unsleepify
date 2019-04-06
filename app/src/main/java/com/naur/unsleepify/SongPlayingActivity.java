package com.naur.unsleepify;

import android.app.Activity;
import android.widget.TextView;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;

public class SongPlayingActivity extends Activity {
    public static final String CLIENT_ID = "6e71d381582a43f2aa3c0366bbe48ea3";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private TextView view;
    private SpotifyAppRemote spotifyAppRemote;

    // todo this will be removed once playlist is made configurable
    private String playlistId = "3pBnQakqa3Cd13p4qQP5Rn";

    @Override
    protected void onStart() {
        super.onStart();
        setContentView(R.layout.activity_song_playing);
        view = findViewById(R.id.songDetails);

        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemoteParam) {
                        spotifyAppRemote = spotifyAppRemoteParam;
                        connected();
                    }

                    public void onFailure(Throwable throwable) {}
                });
    }


    private void connected() {
        spotifyAppRemote.getPlayerApi().setShuffle(true);
        spotifyAppRemote.getPlayerApi().play("spotify:playlist:" + playlistId);

        startActivity(getPackageManager().getLaunchIntentForPackage("com.spotify.music"));
        spotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    @Override
                    public void onEvent(PlayerState playerState) {
                        if (!playerState.isPaused) {
                            spotifyAppRemote.getPlayerApi().setShuffle(false);

                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        SpotifyAppRemote.disconnect(spotifyAppRemote);
        super.onDestroy();
    }
}