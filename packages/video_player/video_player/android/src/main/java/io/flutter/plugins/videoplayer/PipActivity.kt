package io.flutter.plugins.videoplayer

import android.app.PictureInPictureParams
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView

class PipActivity : AppCompatActivity() {

    lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pip)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            playerView = findViewById(R.id.player_view)
            playerView.player = BackgroundModeManager.getInstance().player
            playerView.controllerAutoShow = false
            this.enterPictureInPictureMode(PictureInPictureParams.Builder().build())
        } else
            finish()
    }
}