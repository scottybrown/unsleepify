package com.naur.unsleepify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

public class SongPlayingActivity extends Activity implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback {
    public static final String CLIENT_ID = "6e71d381582a43f2aa3c0366bbe48ea3";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private Player musicPlayer;
    private static final int REQUEST_CODE = 1337;
    private TextView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_playing);
        view = (TextView) findViewById(R.id.songDetails);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        musicPlayer = spotifyPlayer;
                        musicPlayer.addConnectionStateCallback(SongPlayingActivity.this);
                        musicPlayer.addNotificationCallback(SongPlayingActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                    }
                });
            }
        }
    }

    private void toastify(String text) {
        Toast.makeText(this.getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    private void updateTextToSongInformation() {
        if (musicPlayer.getMetadata().currentTrack != null) {
            view.setText(musicPlayer.getMetadata().currentTrack.artistName + " - " + musicPlayer.getMetadata().currentTrack.name);
        }
    }

    @Override
    public void onLoggedIn() {
        musicPlayer.playUri(null, "spotify:playlist:3pBnQakqa3Cd13p4qQP5Rn", 0, 0);
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        if(PlayerEvent.kSpPlaybackNotifyTrackChanged.equals(playerEvent)) {
            updateTextToSongInformation();
        }
    }

    @Override
    public void onPlaybackError(Error error) {
    }

    @Override
    public void onLoggedOut() {
    }

    @Override
    public void onLoginFailed(Error var1) {
    }

    @Override
    public void onTemporaryError() {
    }

    @Override
    public void onConnectionMessage(String message) {
    }
}