package io.flutter.plugins.videoplayer

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.os.IBinder
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerNotificationManager

class PlayerNotificationService : Service() {

    private lateinit var videoPlayer: VideoPlayer
    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var playerNotificationManager: PlayerNotificationManager

    private var notificationId = 123;
    private var channelId = "channelId"

    override fun onCreate() {
        super.onCreate()
        if (BackgroundModeManager.getInstance().player != null) {
            videoPlayer = BackgroundModeManager.getInstance().player!!
            exoPlayer = videoPlayer.exoPlayer
        } else {
            stopSelf()
            return
        }

        playerNotificationManager = PlayerNotificationManager.createWithNotificationChannel(
                this,
                channelId,
                R.string.channel_name,
                R.string.channel_desc,
                notificationId,
                object : PlayerNotificationManager.MediaDescriptionAdapter {
                    override fun createCurrentContentIntent(player: Player): PendingIntent? {
                        return null
//                        val intent = Intent(context, AudioPlayActivity::class.java);
//                        return PendingIntent.getActivity(
//                                context, 0, intent,
//                                PendingIntent.FLAG_UPDATE_CURRENT
//                        )
                    }

                    override fun getCurrentContentText(player: Player): String? {
                        return "Description"
                    }

                    override fun getCurrentContentTitle(player: Player): String {
                        return "Title"
                    }

                    override fun getCurrentLargeIcon(
                            player: Player,
                            callback: PlayerNotificationManager.BitmapCallback
                    ): Bitmap? {
                        return null
                    }
                },
                object : PlayerNotificationManager.NotificationListener {

                    override fun onNotificationPosted(
                            notificationId: Int,
                            notification: Notification,
                            onGoing: Boolean) {
                        startForeground(notificationId, notification)
                    }

                    override fun onNotificationCancelled(
                            notificationId: Int,
                            dismissedByUser: Boolean
                    ) {
                        stopSelf()
                    }

                }
        )
        playerNotificationManager.setPlayer(exoPlayer)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        BackgroundModeManager.getInstance().player = null
        super.onDestroy()
    }

    //removing service when user swipe out our app
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }
}