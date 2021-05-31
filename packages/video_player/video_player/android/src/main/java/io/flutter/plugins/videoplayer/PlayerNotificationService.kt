package io.flutter.plugins.videoplayer

import android.annotation.SuppressLint
import android.app.*
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import io.flutter.embedding.engine.systemchannels.SettingsChannel.CHANNEL_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL


private var NOTIFICATION_ID = 1
private var CHANNEL_ID = "VideoPlayerChannelId"

class PlayerNotificationService : Service() {

    private lateinit var videoPlayer: VideoPlayer
    private lateinit var exoPlayer: SimpleExoPlayer
    private var mediaSession: MediaSessionCompat? = null
    private var sessionConnector: MediaSessionConnector? = null
    private lateinit var notificationManager: NotificationManager


    private var videoBitmap: Bitmap? = null
    private lateinit var broadcastReceiver: BroadcastReceiver
    private var contentIntent: PendingIntent? = null

    private var finish = false

    companion object {
        private var activity: Activity? = null
        fun init(activity: Activity?) {
            this.activity = activity
        }
    }

    private fun startCommand() {
        if (BackgroundModeManager.getInstance().player != null) {
            videoPlayer = BackgroundModeManager.getInstance().player!!
            exoPlayer = videoPlayer.exoPlayer
        } else {
            initNotificationManager()
            startForeground(NOTIFICATION_ID, createNotification().build())
            stopSelf()
            return
        }
        if (activity != null){
            val intent = Intent(activity, activity?.javaClass)
            contentIntent = PendingIntent.getActivity(activity, 0, intent, FLAG_UPDATE_CURRENT)
        }
        initNotificationManager()
        startForeground(NOTIFICATION_ID, createNotification().build())
        applyImageUrl(videoPlayer.previewUrl)
        setupBroadcastReceiver()
        exoPlayer.addListener(
                object : Player.EventListener {
                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        updateNotification()
                    }


                    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                        Log.d(DEBUG_TAG, playbackState.toString())
                        if (playbackState == Player.STATE_IDLE) {
                            Log.d(DEBUG_TAG, "STATE_IDLE")
                            stopSelf()
                        } else if (playbackState == Player.STATE_ENDED) {
                            videoPlayer.seekTo(0)
                            videoPlayer.pause()
                        }
                    }
                })
    }

    private fun initNotificationManager() {
        notificationManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getSystemService(NotificationManager::class.java)
        } else {
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }

        mediaSession = MediaSessionCompat(this, "PlayerNotificationService")
        mediaSession?.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)
        mediaSession?.isActive = true
        mediaSession?.setPlaybackState(PlaybackStateCompat.Builder()
                .setState(PlaybackStateCompat.STATE_NONE, -1, 1f)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO
                        or PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PAUSE // was play and pause now play/pause
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                        or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                        or PlaybackStateCompat.ACTION_SET_REPEAT_MODE
                        or PlaybackStateCompat.ACTION_STOP)
                .build())

        if (mediaSession != null && this::exoPlayer.isInitialized) {
            sessionConnector = MediaSessionConnector(mediaSession!!)
            sessionConnector?.setPlayer(exoPlayer)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    private fun createNotification() : NotificationCompat.Builder {
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this,
                CHANNEL_ID)
        builder.setContentIntent(contentIntent)

        val style = androidx.media.app.NotificationCompat.MediaStyle()
        style.setShowActionsInCompactView(0)
        style.setShowCancelButton(true)
        style.setCancelButtonIntent(PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                Intent(ACTION_CLOSE), FLAG_UPDATE_CURRENT))
        builder.setStyle(style
                .setMediaSession(mediaSession?.sessionToken))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
                .setShowWhen(false)
                .setOngoing(true)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.exo_controls_play)
                .setDeleteIntent(PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                        Intent(ACTION_CLOSE), FLAG_UPDATE_CURRENT))
        if (this::videoPlayer.isInitialized && videoPlayer != null){
            builder.setContentTitle(videoPlayer.title)
            builder.setContentText(videoPlayer.description)
            builder.setSubText(videoPlayer.title)
            builder.setLargeIcon(videoBitmap)
            updateActions(builder)
        }
        return builder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(){
        val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(serviceChannel)
    }

    private fun applyImageUrl(
            imageUrl: String
    ) = runBlocking {
        withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
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

    private fun updateNotification() {
        if (finish)
            return
        val notification = createNotification()
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    private fun onBroadcastReceived(intent: Intent?) {
        if (intent == null || intent.action == null) {
            return
        }
        Log.d(DEBUG_TAG, "onBroadcastReceived() called with: intent = [$intent]")
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
            ACTION_UPDATE_NOTIFICATION -> {
                updateNotification()
            }
            ACTION_EXIT_PIP -> {
                videoPlayer.pause()
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
        intentFilter.addAction(ACTION_UPDATE_NOTIFICATION)
        intentFilter.addAction(ACTION_EXIT_PIP)
        this.registerReceiver(broadcastReceiver, intentFilter)
    }

    private fun unregisterBroadcastReceiver() {
        if (!this::broadcastReceiver.isInitialized)
            return
        try {
            this.unregisterReceiver(broadcastReceiver)
        } catch (unregisteredException: IllegalArgumentException) {
            Log.w(DEBUG_TAG, "Broadcast receiver already unregistered: "
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
                PendingIntent.getBroadcast(this, NOTIFICATION_ID,
                        Intent(intentAction), FLAG_UPDATE_CURRENT))
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            return START_NOT_STICKY;
        }
        val command = intent.getIntExtra(MAIN_SERVICE_COMMAND_KEY, -1);

        if (command == MAIN_SERVICE_START_COMMAND) {
            startCommand();
            return START_STICKY;
        }
        return START_NOT_STICKY;
    }

    override fun onDestroy() {
        unregisterBroadcastReceiver()
        contentIntent = null
        activity = null
        Log.d(DEBUG_TAG, "onDestroy()")
        finish = true
        NotificationManagerCompat.from(this).cancel(NOTIFICATION_ID)
        stopForeground(true)
        sessionConnector?.setPlayer(null)
        mediaSession?.isActive = false
        mediaSession?.release()
        mediaSession = null
        sessionConnector = null
        BackgroundModeManager.getInstance().player = null
        stopSelf()
        super.onDestroy()
    }


    //removing service when user swipe out our app
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

}



const val ACTION_CLOSE: String = "PlayerNotificationService.CLOSE"
const val ACTION_PLAY_PAUSE: String = "PlayerNotificationService.PLAY_PAUSE"
const val ACTION_UPDATE_NOTIFICATION: String = "PlayerNotificationService.UPDATE_NOTIFICATION"
const val ACTION_EXIT_PIP: String = "PlayerNotificationService.ACTION_EXIT_PIP"
const val MAIN_SERVICE_COMMAND_KEY: String = "MAIN_SERVICE_COMMAND_KEY"
const val MAIN_SERVICE_START_COMMAND: Int = 1
const val DEBUG_TAG: String = "VideoPlayerService"