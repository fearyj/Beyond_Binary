package com.beyondbinary.app.fyp;

import android.content.Context;

import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

public class ExoPlayerManager {

    private static ExoPlayerManager instance;
    private ExoPlayer player;
    private PlayerView currentPlayerView;

    public static synchronized ExoPlayerManager getInstance(Context context) {
        if (instance == null) {
            instance = new ExoPlayerManager(context.getApplicationContext());
        }
        return instance;
    }

    private ExoPlayerManager(Context context) {
        player = new ExoPlayer.Builder(context).build();
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
    }

    @OptIn(markerClass = UnstableApi.class)
    public void attach(PlayerView playerView, String url) {
        if (currentPlayerView != null && currentPlayerView != playerView) {
            currentPlayerView.setPlayer(null);
        }
        currentPlayerView = playerView;
        playerView.setPlayer(player);

        MediaItem mediaItem = MediaItem.fromUri(url);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    @OptIn(markerClass = UnstableApi.class)
    public void detach(PlayerView playerView) {
        if (currentPlayerView == playerView) {
            player.pause();
            playerView.setPlayer(null);
            currentPlayerView = null;
        }
    }

    public void release() {
        if (player != null) {
            player.release();
            player = null;
        }
        instance = null;
    }
}
