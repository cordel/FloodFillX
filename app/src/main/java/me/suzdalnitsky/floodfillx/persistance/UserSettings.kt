package me.suzdalnitsky.floodfillx.persistance

import me.suzdalnitsky.floodfillx.ui.SelectableAlgorithm

data class UserSettings(
    val width: Int,
    val height: Int,
    val speed: Int,
    val algorithm: SelectableAlgorithm
) {

    companion object {
        const val DEFAULT_WIDTH = 256
        const val DEFAULT_HEIGHT = 256
        const val DEFAULT_SPEED = 100
        val DEFAULT_ALGORITHM = SelectableAlgorithm.BFS
    }
}