package tech.caaa.aircraft.bullets

import tech.caaa.aircraft.BoxedCollidable
import tech.caaa.aircraft.aircrafts.BaseAircraft
import tech.caaa.aircraft.common.Disposable
import tech.caaa.aircraft.common.Movable


interface Shootable {
    fun shoot() : List<BaseBullet>
}

abstract class BaseBullet(width: Float, height: Float) : BoxedCollidable(width, height), Movable, Disposable {
    abstract fun hit(other: BaseAircraft)
    protected var dead = false
    override fun onOutScreen() {  dead = true  }
    override fun isDead(): Boolean {  return this.dead  }
    override fun onDispose() {}
}

// regular bullet with delta speed movement
abstract class RegularBullet(override var x: Float, override var y: Float, var spdX: Float, val spdY: Float, width: Float, height:Float) : BaseBullet(width,height) {
    override fun move() {
        this.x += this.spdX
        this.y += this.spdY
    }
}

class HeroBullet(private val damage: Float, x: Float, y: Float, spdX: Float, spdY: Float) : RegularBullet(x,y,spdX,spdY, width, height) {
    companion object {
        const val width = 12F
        const val height = 12F
    }

    override fun hit(other: BaseAircraft) {
        other.addHP(-damage)
        dead = true
    }
}
