package me.suzdalnitsky.floodfillx

import android.graphics.Point
import me.suzdalnitsky.floodfillx.algorithm.Algorithm

object StaticStore {

    private var isInit = false

    var points: MutableMap<Point, Boolean> = mutableMapOf()
    var algorithm: Algorithm? = null

    fun assureInit(width: Int, height: Int) {
        if (isInit) return

        points = PointRandomizer.randomizePoints(width = width, height = height)
        isInit = true
    }
}