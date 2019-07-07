package me.suzdalnitsky.floodfillx.algorithm

import android.graphics.Point
import me.suzdalnitsky.floodfillx.bot
import me.suzdalnitsky.floodfillx.consume
import me.suzdalnitsky.floodfillx.left
import me.suzdalnitsky.floodfillx.right
import me.suzdalnitsky.floodfillx.top
import java.util.LinkedList
import kotlin.random.Random

class RandomPickAlgorithm(
    private val points: MutableMap<Point, Boolean>,
    start: Point
) : Algorithm {

    private val list = LinkedList<Point>().apply { add(start) }
    private val isInverse = !points.getValue(start)
    private val random = Random(System.currentTimeMillis())

    override fun performStep(): Boolean {
        if (list.isEmpty()) return false

        val point = list.removeAt(random.nextInt(list.size))

        tryToPush(point.right)
        tryToPush(point.bot)
        tryToPush(point.left)
        tryToPush(point.top)

        return consume { points[point] = isInverse }
    }

    private fun tryToPush(point: Point) {
        if (list.contains(point)) return
        val pointValue = points[point] ?: return

        if (pointValue == !isInverse) list.push(point)
    }
}