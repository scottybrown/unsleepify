package com.naur.unsleepify;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
//need to try the sample app. if it works, reverse engineer it.

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;

import java.util.List;
import java.util.Random;

public class SongPlayingActivity extends Activity {
    public static final String CLIENT_ID = "6e71d381582a43f2aa3c0366bbe48ea3";
    private static final String REDIRECT_URI = "http://localhost:8888/callback";
    private static final int REQUEST_CODE = 1337;
    private TextView view;    private SpotifyAppRemote mSpotifyAppRemote;

    // todo these will be removed once playlist is made configurable
    private String playlistId = "3pBnQakqa3Cd13p4qQP5Rn";
    private String playlistUserId = "soundrop";

    @Override
    protected void onStart( ) {
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

            // todo break up
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "scott"+"Connected! Yay!");

                        // Now you can start interacting with App Remote
                        connected();

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", "scott"+throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }



    private void connected() {
        // Play a playlist
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:"+playlistId);

        // Subscribe to PlayerState
        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    @Override
                    public void onEvent(PlayerState playerState) {
                        final Track track = playerState.track;
                        if (track != null) {
                            Log.d("MainActivity", "scott"+track.name + " by " + track.artist.name);
                        }
                    }
                });
    }


    @Override
    protected void onDestroy() {
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        super.onDestroy();
    }

//    private void updateTextToSongInformation() {
//        if (musicPlayer.getMetadata().currentTrack != null) {
//            view.setText(musicPlayer.getMetadata().currentTrack.artistName + " - " + musicPlayer.getMetadata().currentTrack.name);
//        }
//    }

//    private void playPlaylistInOrder(final Player musicPlayer, int index) {
//        musicPlayer.playUri(null, "spotify:playlist:"+playlistId, index, 0);
//    }
//
//    private void playRandomSongFromPlaylist(final Player musicPlayer) {
//        new AsyncTask<Player, Void, Void>() {
//            @Override
//            protected Void doInBackground(Player... strings) {
//
//                List<PlaylistTrack> playlistTracks = spotifyApi.getService().getPlaylistTracks(playlistUserId, playlistId).items;
//                int randomTrackIndex = new Random().nextInt(playlistTracks.size());
//                playPlaylistInOrder(musicPlayer, randomTrackIndex);
//                return null;
//            }
//        }.execute(musicPlayer);
//    }

//    @Override
//    public void onLoggedIn() {
//        musicPlayer.setShuffle(null, true);
//        playRandomSongFromPlaylist(musicPlayer);
//    }
//
//    @Override
//    public void onPlaybackEvent(PlayerEvent playerEvent) {
//        if (PlayerEvent.kSpPlaybackNotifyTrackChanged.equals(playerEvent)) {
//            updateTextToSongInformation();
//        }
//
//    }
//
//    @Override
//    public void onPlaybackError(Error error) {
//        if (error == Error.kSpErrorFailed) {
//            Utils.toastify("Error playing, perhaps we got to the end of the playlist. Trying another track.", this);
//        } else {
//            Utils.toastify("Error playing, retrying. Details: "+error.name()+": "+error.toString(), this);
//        }
//        playRandomSongFromPlaylist(musicPlayer);
//    }
//
//    @Override
//    public void onLoggedOut() {
//    }
//
//    @Override
//    public void onLoginFailed(Error var1) {
//    }
//
//    @Override
//    public void onTemporaryError() {
//    }
//
//    @Override
//    public void onConnectionMessage(String message) {
//    }
}