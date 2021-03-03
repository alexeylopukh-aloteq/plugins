package io.flutter.plugins.videoplayer.player_view;

import android.content.Context;

import com.google.android.exoplayer2.SimpleExoPlayer;

import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class VideoPlayerFactory extends PlatformViewFactory {

    private final SimpleExoPlayer exoPlayer;
    public final Long id;

    public VideoPlayerFactory(SimpleExoPlayer exoPlayer, Long id) {
        super(StandardMessageCodec.INSTANCE);
        this.id = id;
        this.exoPlayer = exoPlayer;
    }

    @Override
    public PlatformView create(Context context, int id, Object o) {
        return new VideoPlayerTextureView(context, exoPlayer);
    }
}
