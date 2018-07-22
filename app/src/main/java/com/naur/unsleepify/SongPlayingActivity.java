package com.naur.unsleepify;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
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

import java.util.List;
import java.util.Random;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistTrack;

import static com.naur.unsleepify.SongPlayingActivity.CLIENT_ID;

public class SongPlayingActivity extends Activity implements SpotifyPlayer.NotificationCallback, ConnectionStateCallback {
    public static final String CLIENT_ID = "6e71d381582a43f2aa3c0366bbe48ea3";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private Player musicPlayer;
    private static final int REQUEST_CODE = 1337;
    private TextView view;
    private SpotifyApi spotifyApi = new SpotifyApi();
    private String playlistId = "3pBnQakqa3Cd13p4qQP5Rn";// todo shouldn't be here
    private String playlistUserId = "soundrop";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_playing);
        view = findViewById(R.id.songDetails);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == REQUEST_CODE) {
            final AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                spotifyApi.setAccessToken(response.getAccessToken());
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                SpotifyPlayer player = Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {

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
        // todo don't duplicate
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

    private void playPlaylistInOrder(final Player musicPlayer, int index) {
        musicPlayer.playUri(null, "spotify:playlist:3pBnQakqa3Cd13p4qQP5Rn", index, 0);
    }

    private void playRandomSongFromPlaylist(final Player musicPlayer) {
        new AsyncTask<Player, Void, Void>() {
            @Override
            protected Void doInBackground(Player... strings) {

                List<PlaylistTrack> playlistTracks = spotifyApi.getService().getPlaylistTracks(playlistUserId, playlistId).items;
                int randomTrackIndex = new Random().nextInt(playlistTracks.size());
                playPlaylistInOrder(musicPlayer, randomTrackIndex);
                return null;
            }
        }.execute(musicPlayer);
    }

    @Override
    public void onLoggedIn() {
        musicPlayer.setShuffle(null, true);
        playRandomSongFromPlaylist(musicPlayer);
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        if (PlayerEvent.kSpPlaybackNotifyTrackChanged.equals(playerEvent)) {
            updateTextToSongInformation();
        }

    }

    @Override
    public void onPlaybackError(Error error) {
        if (error == Error.kSpErrorFailed) {
            toastify("Error playing, perhaps we got to the end of the playlist. Trying another track.");
            playRandomSongFromPlaylist(musicPlayer);
        }
    }

    @Override
    public void onLoggedOut() {
    }

    @Override
    public void onLoginFailed(Error var1) {
    }

    @Override
    public void onTemporaryError() {}

    @Override
    public void onConnectionMessage(String message) {
    }

}