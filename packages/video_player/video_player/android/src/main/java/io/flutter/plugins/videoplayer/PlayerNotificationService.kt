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

    private lateinit var mPlayer: SimpleExoPlayer
    private lateinit var playerNotificationManager: PlayerNotificationManager

    private var notificationId = 123;
    private var channelId = "channelId"

    override fun onCreate() {
        super.onCreate()
        if (BackgroundModeManager.getInstance().player != null) {
            mPlayer = BackgroundModeManager.getInstance().player!!
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
        playerNotificationManager.setPlayer(mPlayer)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    // concatenatingMediaSource to pass media as a list,
    // so that we can easily prev, next
//    private fun getListOfMediaSource(): ConcatenatingMediaSource {
//        val mediaUrlList = ArrayList<String>()
//        mediaUrlList.add("https://bitdash-a.akamaihd.net/content/MI201109210084_1/m3u8s/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.m3u8")
//        mediaUrlList.add("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8")
//        mediaUrlList.add("http://d3rlna7iyyu8wu.cloudfront.net/skip_armstrong/skip_armstrong_stereo_subs.m3u8")
//        mediaUrlList.add("https://moctobpltc-i.akamaihd.net/hls/live/571329/eight/playlist.m3u8")
//        mediaUrlList.add("https://multiplatform-f.akamaihd.net/i/multi/will/bunny/big_buck_bunny_,640x360_400,640x360_700,640x360_1000,950x540_1500,.f4v.csmil/master.m3u8")
//
//        val concatenatingMediaSource = ConcatenatingMediaSource()
//        for (mediaUrl in mediaUrlList) {
//            concatenatingMediaSource.addMediaSource(buildMediaSource(mediaUrl))
//        }
//
//        return concatenatingMediaSource
//
//    }

    //build media source to player
//    private fun buildMediaSource(videoUrl: String): HlsMediaSource? {
//        val uri = Uri.parse(videoUrl)
//        // Create a HLS media source pointing to a playlist uri.
//        return HlsMediaSource.Factory(dataSourceFactory).createMediaSource(uri)
//    }

    // detach player
    override fun onDestroy() {
//        playerNotificationManager.setPlayer(null)
//        mPlayer.release()
        super.onDestroy()
    }

    //removing service when user swipe out our app
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }
}