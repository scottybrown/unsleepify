package com.naur.unsleepify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Empty;
import com.spotify.protocol.types.PlayerState;

import java.util.Arrays;
import java.util.List;

import static com.naur.unsleepify.MainActivity.EXCLUDED_ARTISTS_KEY;
import static com.naur.unsleepify.MainActivity.SAVED_PLAYLIST_ID_KEY;

public class SongPlayingActivity extends Activity {
    public static final String CLIENT_ID = "6e71d381582a43f2aa3c0366bbe48ea3";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private SpotifyAppRemote spotifyAppRemote;

    @Override
    protected void onResume() {
        super.onResume();
        Intent startIntent = new Intent(this, MainActivity.class);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_playing);

        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID).setRedirectUri(REDIRECT_URI).showAuthView(true).build();

        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
            public void onConnected(SpotifyAppRemote spotifyAppRemoteParam) {
                spotifyAppRemote = spotifyAppRemoteParam;
                spotifyAppRemote.getConnectApi().connectSwitchToLocalDevice();
                connected();
            }

            public void onFailure(Throwable throwable) {
            }
        });
    }

    private boolean isTrackExcludedByArtist(String artistNameRaw, List<String> excludedArtistsList) {
        if (excludedArtistsList.isEmpty()) {
            return false;
        }
        for (String excludedArtistRaw : excludedArtistsList) {
            String excludedArtist = excludedArtistRaw.trim().toLowerCase();
            String artistName = artistNameRaw.trim().toLowerCase();
            if (!(excludedArtist.isEmpty()) && artistName.contains(excludedArtist)) {
                return true;
            }
        }
        return false;
    }

    private void connected() {
        // otherwise, below when we switch off shuffle, if music is already playing it will do it too early - before the song has actually been chosen. Hence we'll get the first song in the playlist.
        // Shouldn't happen if it's actually waking you up, more of an inconvenience.
        spotifyAppRemote.getPlayerApi().pause().setResultCallback(new CallResult.ResultCallback<Empty>() {
            @Override
            public void onResult(final Empty empty) {
                spotifyAppRemote.getPlayerApi().setShuffle(true);
                String playlistId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(SAVED_PLAYLIST_ID_KEY, getString(R.string.default_playlist_id));
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

                                String excludedArtistsString = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(EXCLUDED_ARTISTS_KEY, "");
                                List<String> excludedArtistsList = Arrays.asList(excludedArtistsString.split(","));
                                if (isTrackExcludedByArtist(playerState.track.artist.name, excludedArtistsList)) {
                                    spotifyAppRemote.getPlayerApi().skipNext();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onDestroy() {
        SpotifyAppRemote.disconnect(spotifyAppRemote);
        super.onDestroy();
    }
}