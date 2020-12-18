package io.flutter.plugins.videoplayer

import com.google.android.exoplayer2.SimpleExoPlayer

class BackgroundModeManager private constructor() {
    companion object {
        private var instance: BackgroundModeManager? = null
        fun getInstance(): BackgroundModeManager {
            if (instance == null) {
                instance = BackgroundModeManager()
            }
            return instance as BackgroundModeManager
        }
    }

    init {

    }

    var player: SimpleExoPlayer? = null
        get() = field
        set(value) {
            field = value
        }
}