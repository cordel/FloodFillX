package me.suzdalnitsky.floodfillx.persistance

import android.content.Context
import me.suzdalnitsky.floodfillx.algorithm.SelectableAlgorithm

class SettingsStore(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var userSettings: UserSettings
        get() = UserSettings(
            width = prefs.getInt(KEY_WIDTH, UserSettings.DEFAULT_WIDTH),
            height = prefs.getInt(KEY_HEIGHT, UserSettings.DEFAULT_HEIGHT),
            speed = prefs.getInt(KEY_SPEED, UserSettings.DEFAULT_SPEED),
            algorithm = prefs.getInt(
                KEY_ALGORITHM,
                UserSettings.DEFAULT_ALGORITHM_ORDINAL
            )
                .let { SelectableAlgorithm.values()[it] }
        )
        set(value) {
            prefs.edit().apply {
                putInt(KEY_WIDTH, value.width)
                putInt(KEY_HEIGHT, value.height)
                putInt(KEY_SPEED, value.speed)
                putInt(KEY_ALGORITHM, value.algorithm.ordinal)
            }
                .apply()
        }

    inline fun updateSettings(update: UserSettings.() -> UserSettings) {
        userSettings = update.invoke(userSettings)
    }

    companion object {
        private const val PREFS_NAME = "PREFS_NAME"
        private const val KEY_WIDTH = "KEY_WIDTH"
        private const val KEY_HEIGHT = "KEY_HEIGHT"
        private const val KEY_SPEED = "KEY_SPEED"
        private const val KEY_ALGORITHM = "KEY_ALGORITHM"
    }
}