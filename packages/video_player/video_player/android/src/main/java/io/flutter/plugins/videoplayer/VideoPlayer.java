package io.flutter.plugins.videoplayer;

import static com.google.android.exoplayer2.Player.REPEAT_MODE_ALL;
import static com.google.android.exoplayer2.Player.REPEAT_MODE_OFF;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;

import androidx.annotation.RequiresApi;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerNotificationManager;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import io.flutter.plugin.common.EventChannel;
import io.flutter.view.TextureRegistry;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

final public class VideoPlayer {
  private static final String FORMAT_SS = "ss";
  private static final String FORMAT_DASH = "dash";
  private static final String FORMAT_HLS = "hls";
  private static final String FORMAT_OTHER = "other";

  private final SimpleExoPlayer exoPlayer;

  private Surface surface;

  final TextureRegistry.SurfaceTextureEntry textureEntry;

  private final QueuingEventSink eventSink = new QueuingEventSink();

  private final EventChannel eventChannel;

  private boolean isInitialized = false;

  private final VideoPlayerOptions options;

  Boolean backgroundMode = false;
  
  public final String title;
  public final String description;
  public final String previewUrl;

  private int usageCount = 1;

  private AudioManager audioManager;
  private AudioFocusRequest audioFocusRequest;
  private DefaultTrackSelector trackSelector;
  private DefaultTrackSelector.Parameters defaultTrackParam;

  public void incUsageCount() {
    usageCount++;
  }
  

  VideoPlayer(
      Context context,
      EventChannel eventChannel,
      TextureRegistry.SurfaceTextureEntry textureEntry,
      String dataSource,
      String formatHint,
      VideoPlayerOptions options,
      String title,
      String description,
      String previewUrl) {
    this.eventChannel = eventChannel;
    this.textureEntry = textureEntry;
    this.options = options;
    this.title = title;
    this.description = description;
    this.previewUrl = previewUrl;

    trackSelector = new DefaultTrackSelector(context);
    defaultTrackParam = trackSelector.buildUponParameters().setMaxVideoSize(426, 240)
            .setForceLowestBitrate(true)
            .setForceHighestSupportedBitrate(false).build();
    trackSelector.setParameters(defaultTrackParam);
    exoPlayer = new SimpleExoPlayer.Builder(context).setTrackSelector(trackSelector).build();

    Uri uri = Uri.parse(dataSource);

    DataSource.Factory dataSourceFactory;
    if (isHTTP(uri)) {
      dataSourceFactory =
          new DefaultHttpDataSourceFactory(
              "ExoPlayer",
              null,
              DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
              DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
              true);
    } else {
      dataSourceFactory = new DefaultDataSourceFactory(context, "ExoPlayer");
    }

    MediaSource mediaSource = buildMediaSource(uri, dataSourceFactory, formatHint, context);
    exoPlayer.setMediaSource(mediaSource);
    exoPlayer.prepare();

    setupVideoPlayer(eventChannel, textureEntry);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      initAudioFocus(context);
    }
  }

  private static boolean isHTTP(Uri uri) {
    if (uri == null || uri.getScheme() == null) {
      return false;
    }
    String scheme = uri.getScheme();
    return scheme.equals("http") || scheme.equals("https");
  }

  private MediaSource buildMediaSource(
      Uri uri, DataSource.Factory mediaDataSourceFactory, String formatHint, Context context) {
    int type;
    if (formatHint == null) {
      type = Util.inferContentType(uri.getLastPathSegment());
    } else {
      switch (formatHint) {
        case FORMAT_SS:
          type = C.TYPE_SS;
          break;
        case FORMAT_DASH:
          type = C.TYPE_DASH;
          break;
        case FORMAT_HLS:
          type = C.TYPE_HLS;
          break;
        case FORMAT_OTHER:
          type = C.TYPE_OTHER;
          break;
        default:
          type = -1;
          break;
      }
    }
    switch (type) {
      case C.TYPE_SS:
        return new SsMediaSource.Factory(
                new DefaultSsChunkSource.Factory(mediaDataSourceFactory),
                new DefaultDataSourceFactory(context, null, mediaDataSourceFactory))
            .createMediaSource(MediaItem.fromUri(uri));
      case C.TYPE_DASH:
        return new DashMediaSource.Factory(
                new DefaultDashChunkSource.Factory(mediaDataSourceFactory),
                new DefaultDataSourceFactory(context, null, mediaDataSourceFactory))
            .createMediaSource(MediaItem.fromUri(uri));
      case C.TYPE_HLS:
        return new HlsMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri));
      case C.TYPE_OTHER:
        return new ProgressiveMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri));
      default:
        {
          throw new IllegalStateException("Unsupported type: " + type);
        }
    }
  }

  private void setupVideoPlayer(
      EventChannel eventChannel, TextureRegistry.SurfaceTextureEntry textureEntry) {

    eventChannel.setStreamHandler(
        new EventChannel.StreamHandler() {
          @Override
          public void onListen(Object o, EventChannel.EventSink sink) {
            eventSink.setDelegate(sink);
          }

          @Override
          public void onCancel(Object o) {
            eventSink.setDelegate(null);
          }
        });

    surface = new Surface(textureEntry.surfaceTexture());
    exoPlayer.setVideoSurface(surface);
    setAudioAttributes(exoPlayer, options.mixWithOthers);

    exoPlayer.addListener(
        new EventListener() {

          @Override
          public void onIsPlayingChanged(boolean isPlaying) {
            if (isPlaying && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    && audioManager != null && exoPlayer.getVolume() > 0) {
              audioManager.requestAudioFocus(audioFocusRequest);
            }
            Map<String, Object> event = new HashMap<>();
            event.put("event", "isPlaying");
            event.put("isPlaying", isPlaying);
            eventSink.success(event);

          }

          @Override
          public void onPlaybackStateChanged(final int playbackState) {
            if (playbackState == Player.STATE_BUFFERING) {
              sendBufferingUpdate();
            } else if (playbackState == Player.STATE_READY) {
              if (!isInitialized) {
                isInitialized = true;
                sendInitialized();
              }
            } else if (playbackState == Player.STATE_ENDED) {
              Map<String, Object> event = new HashMap<>();
              event.put("event", "completed");
              eventSink.success(event);
            }
          }

          @Override
          public void onPlayerError(final ExoPlaybackException error) {
            if (eventSink != null) {
              eventSink.error("VideoError", "Video player had error " + error, null);
            }
          }
        });
  }

  void sendBufferingUpdate() {
    Map<String, Object> event = new HashMap<>();
    event.put("event", "bufferingUpdate");
    List<? extends Number> range = Arrays.asList(0, exoPlayer.getBufferedPosition());
    // iOS supports a list of buffered ranges, so here is a list with a single range.
    event.put("values", Collections.singletonList(range));
    eventSink.success(event);
  }

  @SuppressWarnings("deprecation")
  private static void setAudioAttributes(SimpleExoPlayer exoPlayer, boolean isMixMode) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      exoPlayer.setAudioAttributes(
          new com.google.android.exoplayer2.audio.AudioAttributes.Builder()
                  .setContentType(C.CONTENT_TYPE_MOVIE).build(), !isMixMode);
    } else {
      exoPlayer.setAudioStreamType(C.STREAM_TYPE_MUSIC);
    }
  }

  void play() {
    exoPlayer.setPlayWhenReady(true);
  }

  void pause() {
    exoPlayer.setPlayWhenReady(false);
  }

  void setQuality(int maxVideoWidth, int maxVideoHeight){
    DefaultTrackSelector.Parameters parameters = trackSelector.buildUponParameters()
            .setMaxVideoSize(maxVideoWidth, maxVideoHeight)
            .setForceLowestBitrate(maxVideoWidth < 720)
            .setForceHighestSupportedBitrate(maxVideoWidth >= 720)
            .build();
    trackSelector.setParameters(parameters);
  }

  void setLooping(boolean value) {
    exoPlayer.setRepeatMode(value ? REPEAT_MODE_ALL : REPEAT_MODE_OFF);
  }

  void setVolume(double value) {
    if (value > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && audioManager != null && exoPlayer.isPlaying()){
        audioManager.requestAudioFocus(audioFocusRequest);
    }
    float bracketedValue = (float) Math.max(0.0, Math.min(1.0, value));
    exoPlayer.setVolume(bracketedValue);
  }

  void setPlaybackSpeed(double value) {
    // We do not need to consider pitch and skipSilence for now as we do not handle them and
    // therefore never diverge from the default values.
    final PlaybackParameters playbackParameters = new PlaybackParameters(((float) value));

    exoPlayer.setPlaybackParameters(playbackParameters);
  }

  void seekTo(int location) {
    exoPlayer.seekTo(location);
  }

  long getPosition() {
    return exoPlayer.getCurrentPosition();
  }

  @SuppressWarnings("SuspiciousNameCombination")
  void sendInitialized() {
    if (isInitialized) {
      Map<String, Object> event = new HashMap<>();
      event.put("event", "initialized");
      event.put("duration", exoPlayer.getDuration());

      if (exoPlayer.getVideoFormat() != null) {
        Format videoFormat = exoPlayer.getVideoFormat();
        int width = videoFormat.width;
        int height = videoFormat.height;
        int rotationDegrees = videoFormat.rotationDegrees;
        // Switch the width/height if video was taken in portrait mode
        if (rotationDegrees == 90 || rotationDegrees == 270) {
          width = exoPlayer.getVideoFormat().height;
          height = exoPlayer.getVideoFormat().width;
        }
        event.put("isBackgroundMode", backgroundMode);
        event.put("width", width);
        event.put("height", height);
      }
      eventSink.success(event);
    }
  }

  SimpleExoPlayer getExoPlayer() {
    return exoPlayer;
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private void initAudioFocus(Context context) {
    audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    AudioAttributes mPlaybackAttributes = new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build();
    audioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(mPlaybackAttributes)
            .setAcceptsDelayedFocusGain(true)
            .setWillPauseWhenDucked(true)
            .setOnAudioFocusChangeListener(i -> {
              if (i == AudioManager.AUDIOFOCUS_LOSS && backgroundMode) {
                pause();
              }
            }, new Handler())
            .build();
  }

  void dispose() {
    Log.d("dispose", "call");
    usageCount--;
    if (usageCount > 0)
      return;
    Log.d("dispose", "Start dispose");
    if (isInitialized) {
      exoPlayer.stop();
    }
    try {
    textureEntry.release();
    }catch (Exception ignored){}
    eventChannel.setStreamHandler(null);
    if (surface != null) {
      try {
        surface.release();
      } catch (Exception ignored){}
    }
    if (exoPlayer != null) {
      exoPlayer.release();
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      audioManager.abandonAudioFocusRequest(audioFocusRequest);
      audioManager = null;
    }
  }
}
