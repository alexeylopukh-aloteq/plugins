package io.flutter.plugins.videoplayer

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL


class PlayerNotificationService : Service() {

    private lateinit var videoPlayer: VideoPlayer
    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var sessionConnector: MediaSessionConnector
    private lateinit var notificationManager: NotificationManager

    private var notificationId = 123
    private var channelId = "channelId"
    private var videoBitmap: Bitmap? = null
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate() {
        super.onCreate()
        if (BackgroundModeManager.getInstance().player != null) {
            videoPlayer = BackgroundModeManager.getInstance().player!!
            exoPlayer = videoPlayer.exoPlayer
        } else {
            stopSelf()
            return
        }
        startForeground(notificationId, createNotification().build())
        applyImageUrl("https://pbs.twimg.com/profile_images/1310973573931171845/X1-iVrIv_400x400.jpg")
        setupBroadcastReceiver()
    }

    private fun createNotification() : NotificationCompat.Builder{
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        mediaSession = MediaSessionCompat(applicationContext, "PlayerNotificationService")
        mediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession.isActive = true
        mediaSession.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, -1, 1f)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO
                        or PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PAUSE // was play and pause now play/pause
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        or PlaybackStateCompat.ACTION_SET_REPEAT_MODE
                        or PlaybackStateCompat.ACTION_STOP)
                .build())

        sessionConnector = MediaSessionConnector(mediaSession)
        sessionConnector.setPlayer(exoPlayer)

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext,
                channelId)

        val style = androidx.media.app.NotificationCompat.MediaStyle()
        style.setShowActionsInCompactView(0)
        builder.setStyle(style
                .setMediaSession(mediaSession.sessionToken))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                .setShowWhen(true)
                .setSmallIcon(R.drawable.exo_controls_play)
                .setDeleteIntent(PendingIntent.getBroadcast(applicationContext, notificationId,
                        Intent(ACTION_CLOSE), FLAG_UPDATE_CURRENT))
        builder.setContentTitle("Title")
        builder.setContentText("Text asdjoais dioajsoi djasoi lol")
        builder.setTicker("Ticker")
        builder.setLargeIcon(videoBitmap)

        updateActions(builder)
        return builder
    }

    fun applyImageUrl(
            imageUrl: String
    ) = runBlocking {
        val url = URL(imageUrl)

        withContext(Dispatchers.IO) {
            try {
                val input = url.openStream()
                BitmapFactory.decodeStream(input)
            } catch (e: IOException) {
                null
            }
        }?.let { bitmap ->
            videoBitmap = bitmap
            updateNotification()
        }
    }

    private fun updateNotification(){
        val notification = createNotification()
        notificationManager.notify(notificationId, notification.build())
    }

    private fun onBroadcastReceived(intent: Intent?) {
        if (intent == null || intent.action == null) {
            return
        }
        Log.d(DUBUG_TAG, "onBroadcastReceived() called with: intent = [$intent]")
        when (intent.action) {
            ACTION_CLOSE -> {
                videoPlayer.pause()
                stopSelf()
            }
            ACTION_PLAY_PAUSE -> {
                if (exoPlayer.isPlaying)
                    videoPlayer.pause()
                else videoPlayer.play()
            }
        }
        updateNotification()
    }

    private fun setupBroadcastReceiver() {
        unregisterBroadcastReceiver()
        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                onBroadcastReceived(intent)
            }
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_CLOSE)
        intentFilter.addAction(ACTION_PLAY_PAUSE)
        applicationContext.registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun unregisterBroadcastReceiver() {
        if (!this::broadcastReceiver.isInitialized)
            return
        try {
            applicationContext.unregisterReceiver(broadcastReceiver)
        } catch (unregisteredException: IllegalArgumentException) {
            Log.w(DUBUG_TAG, "Broadcast receiver already unregistered: "
                    + unregisteredException.message)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun updateActions(builder: NotificationCompat.Builder){
        builder.mActions.clear()
        builder.mActions.addAll(getActions())
    }

    private fun getActions() : List<NotificationCompat.Action>{
        val actions = ArrayList<NotificationCompat.Action>()
        val playPauseIcon = if (exoPlayer.isPlaying)
            R.drawable.exo_icon_pause
        else R.drawable.exo_controls_play
        actions.add(getAction(playPauseIcon, "Play/Pause", ACTION_PLAY_PAUSE))
        actions.add(getAction(R.drawable.exo_icon_stop, "Play", ACTION_CLOSE))
        return actions
    }

    private fun getAction(@DrawableRes drawable: Int,
                          title: String,
                          intentAction: String): NotificationCompat.Action {
        return NotificationCompat.Action(drawable, title,
                PendingIntent.getBroadcast(applicationContext, notificationId,
                        Intent(intentAction), FLAG_UPDATE_CURRENT))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        unregisterBroadcastReceiver()
        BackgroundModeManager.getInstance().player = null
        notificationManager.cancel(notificationId)
        super.onDestroy()
    }

    //removing service when user swipe out our app
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }
}


val ACTION_CLOSE: String = "PlayerNotificationService.CLOSE"
val ACTION_PLAY_PAUSE: String = "PlayerNotificationService.PLAY_PAUSE"

val DUBUG_TAG: String = "VideoPlayerService"