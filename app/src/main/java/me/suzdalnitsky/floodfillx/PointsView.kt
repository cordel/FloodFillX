package me.suzdalnitsky.floodfillx

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class PointsView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val paint = Paint().apply { color = Color.BLACK }

    private var mapWidth: Int? = null
    private var mapHeight: Int? = null
    private var points: Map<Point, Boolean>? = null

    private val isInitialized: Boolean
        get() = mapWidth != null && mapHeight != null && points != null

    private val stepX: Float
        get() = width / mapWidth!!.toFloat()

    private val stepY: Float
        get() = height / mapHeight!!.toFloat()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (!isInitialized) return

        val stepX = stepX
        val stepY = stepY

        points!!.forEach { (key, value) ->
            if (value) {
                canvas.drawRect(
                    key.x * stepX,
                    key.y * stepY,
                    key.x.inc() * stepX,
                    key.y.inc() * stepY,
                    paint
                )
            }
        }
    }

    fun init(mapWidth: Int, mapHeight: Int, points: Map<Point, Boolean>) {
        this.mapWidth = mapWidth
        this.mapHeight = mapHeight
        this.points = points
    }

    fun refresh() = invalidate()

    fun setOnPointClickListener(listener: (Point) -> Unit) = setOnTouchListener { _, event ->
        if (event.action != MotionEvent.ACTION_DOWN) return@setOnTouchListener false

        val point = Point(
            (event.x / stepX).toInt(),
            (event.y / stepY).toInt()
        )

        listener(point)
        true
    }
}