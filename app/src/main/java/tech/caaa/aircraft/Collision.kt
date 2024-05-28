package tech.caaa.aircraft

import android.graphics.Rect


fun getHitbox(x: Float, y: Float, width: Float, height: Float): Rect {
    return Rect((x - width * 0.5).toInt(), (y - height * 0.5).toInt(),
        (x + width * 0.5).toInt(), (y + height * 0.5).toInt()
    )
}
abstract class BoxedCollidable(private val width: Float, private val height: Float) {
    abstract var x: Float
    abstract var y: Float
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