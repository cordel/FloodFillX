package me.suzdalnitsky.floodfillx.persistance

data class UserSettings(
    val width: Int,
    val height: Int,
    val speed: Int
) {

    companion object {
        const val DEFAULT_WIDTH = 256
        const val DEFAULT_HEIGHT = 256
        const val DEFAULT_SPEED = 100
    }
}