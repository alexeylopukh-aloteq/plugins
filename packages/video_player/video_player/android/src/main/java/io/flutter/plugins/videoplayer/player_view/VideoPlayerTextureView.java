package io.flutter.plugins.videoplayer.player_view;

import android.content.Context;
import android.view.View;

import com.google.android.exoplayer2.SimpleExoPlayer;

import io.flutter.plugin.platform.PlatformView;

public class VideoPlayerTextureView implements PlatformView {
    private final ExoPlayerNativeView playerNativeView;

    public VideoPlayerTextureView(Context context, SimpleExoPlayer exoPlayer) {
        this.playerNativeView = new ExoPlayerNativeView(context, exoPlayer);
    }

    @Override
    public View getView() {
        return playerNativeView;
    }

    @Override
    public void dispose() {

    }
}
