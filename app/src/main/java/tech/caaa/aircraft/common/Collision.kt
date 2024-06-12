package tech.caaa.aircraft.common

import android.graphics.Rect
import tech.caaa.aircraft.game.Rectangle


fun getHitbox(x: Double, y: Double, width: Double, height: Double): Rectangle {
    return Rectangle(
        (x - width * 0.5).toInt(), (y - height * 0.5).toInt(),
        (x + width * 0.5).toInt(), (y + height * 0.5).toInt()
    )
}

abstract class BoxedCollidable(private val width: Double, private val height: Double) {
    abstract val x: Double
    abstract val y: Double
    fun check(other: BoxedCollidable): Boolean {
        val hitbox = getHitbox(x, y, width, height)
        val hitboxOthers = getHitbox(other.x, other.y, other.width, other.height)
        return hitbox.intersects(hitboxOthers)
    }

    val hitbox: Rectangle
        get() {
            return getHitbox(x, y, width, height)
        }
}