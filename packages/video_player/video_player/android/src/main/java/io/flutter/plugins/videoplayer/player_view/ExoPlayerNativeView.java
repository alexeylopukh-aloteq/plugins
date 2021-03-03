package io.flutter.plugins.videoplayer.player_view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import io.flutter.plugins.videoplayer.R;

public class ExoPlayerNativeView extends ConstraintLayout {

    final SimpleExoPlayer exoPlayer;

    public ExoPlayerNativeView(Context context, SimpleExoPlayer exoPlayer) {
        super(context);
        this.exoPlayer = exoPlayer;
        init(context);
    }

    public ExoPlayerNativeView(Context context, @Nullable AttributeSet attrs, SimpleExoPlayer exoPlayer) {
        super(context, attrs);
        this.exoPlayer = exoPlayer;
        init(context);
    }

    public ExoPlayerNativeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, SimpleExoPlayer exoPlayer) {
        super(context, attrs, defStyleAttr);
        this.exoPlayer = exoPlayer;
        init(context);
    }

    public ExoPlayerNativeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes, SimpleExoPlayer exoPlayer) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.exoPlayer = exoPlayer;
        init(context);
    }

    private void init(Context context){
        inflate(context, R.layout.exo_player_native_view, this);
        PlayerView playerView = findViewById(R.id.player_view);
        playerView.setUseController(false);
        playerView.setPlayer(exoPlayer);
    }
}