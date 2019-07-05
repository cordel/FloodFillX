package me.suzdalnitsky.floodfillx

import android.graphics.Point
import android.util.Log
import kotlin.random.Random

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

inline fun consume(f: () -> Unit) = f().let { true }

fun log(throwable: Throwable) = Log.e("FloodFillX", throwable.message)

val Point.right get() = Point(x, y.inc())
val Point.left get() = Point(x, y.dec())
val Point.top get() = Point(x.inc(), y)
val Point.bot get() = Point(x.dec(), y)
