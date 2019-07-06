package me.suzdalnitsky.floodfillx

import android.graphics.Point
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

inline fun consume(f: () -> Unit) = f().let { true }

inline fun LifecycleOwner.invokeIfResumed(func: () -> Unit) {
    if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
        func.invoke()
    }
}

fun log(throwable: Throwable) = Log.e("FloodFillX", throwable.message)

val Point.right get() = Point(x, y.inc())
val Point.left get() = Point(x, y.dec())
val Point.top get() = Point(x.inc(), y)
val Point.bot get() = Point(x.dec(), y)
