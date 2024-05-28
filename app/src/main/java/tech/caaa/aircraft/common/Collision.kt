package tech.caaa.aircraft.common

import android.graphics.Rect


fun getHitbox(x: Double, y: Double, width: Double, height: Double): Rect {
    return Rect((x - width * 0.5).toInt(), (y - height * 0.5).toInt(),
        (x + width * 0.5).toInt(), (y + height * 0.5).toInt()
    )
}
abstract class BoxedCollidable(private val width: Double, private val height: Double) {
    abstract val x: Double
    abstract val y: Double
    public fun check(other: BoxedCollidable): Boolean {
        val hitbox = getHitbox(x,y,width,height)
        val hitboxOthers = getHitbox(other.x,other.y,other.width,other.height)
        return hitbox.intersects(hitboxOthers.left, hitboxOthers.top, hitboxOthers.right, hitboxOthers.bottom)
    }

    val hitbox: Rect
        get() {
            return getHitbox(x,y,width,height)
        }
}