package me.suzdalnitsky.floodfillx.algorithm

import android.graphics.Point

abstract class Algorithm(
    private val points: MutableMap<Point, Boolean>,
    start: Point
) {

    abstract fun performStep(): Boolean
}