package me.suzdalnitsky.floodfillx.algorithm

import android.graphics.Point
import me.suzdalnitsky.floodfillx.bot
import me.suzdalnitsky.floodfillx.consume
import me.suzdalnitsky.floodfillx.left
import me.suzdalnitsky.floodfillx.right
import me.suzdalnitsky.floodfillx.top
import java.util.LinkedList

class BfsAlgorithm(
    private val points: MutableMap<Point, Boolean>,
    start: Point
) : Algorithm {

    private val queue = LinkedList<Point>().apply { push(start) }
    private val isInverse = !points.getValue(start)

    override fun performStep(): Boolean {
        if (queue.isEmpty()) return false

        val point = queue.removeLast()

        tryToPush(point.right)
        tryToPush(point.bot)
        tryToPush(point.left)
        tryToPush(point.top)

        return consume { points[point] = isInverse }
    }

    private fun tryToPush(point: Point) {
        if (queue.contains(point)) return
        val pointValue = points[point] ?: return

        if (pointValue == !isInverse) queue.push(point)
    }
}