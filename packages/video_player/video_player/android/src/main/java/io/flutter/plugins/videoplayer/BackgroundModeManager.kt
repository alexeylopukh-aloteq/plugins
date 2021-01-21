package io.flutter.plugins.videoplayer


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

    var player: VideoPlayer? = null
        get() = field
        set(value) {
            if (value == null) {
                field?.backgroundMode = false
                field?.dispose()
            } else {
                value.backgroundMode = true
                value.incUsageCount()
            }
            field = value

        }
}