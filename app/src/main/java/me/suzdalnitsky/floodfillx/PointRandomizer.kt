package me.suzdalnitsky.floodfillx

import android.graphics.Point
import kotlin.random.Random

object PointRandomizer {

    fun randomizePoints(width: Int, height: Int): MutableMap<Point, Boolean> {
        val random = Random(System.currentTimeMillis())
        val result = mutableMapOf<Point, Boolean>()

        for (x in 0 until width) {
            for (y in 0 until height) {
                result[Point(x, y)] = random.nextBoolean()
            }
        }

        return result
    }
}