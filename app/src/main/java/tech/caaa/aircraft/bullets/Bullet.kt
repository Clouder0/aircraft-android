package tech.caaa.aircraft.bullets

import tech.caaa.aircraft.aircrafts.BaseAircraft
import tech.caaa.aircraft.aircrafts.HeroAircraft
import tech.caaa.aircraft.common.BoxedCollidable
import tech.caaa.aircraft.common.Disposable
import tech.caaa.aircraft.common.Movable


interface Shootable {
    fun shoot(): List<BaseBullet>
}

abstract class BaseBullet(width: Double, height: Double) : BoxedCollidable(width, height), Movable,
    Disposable {
    abstract fun hit(other: BaseAircraft)
    protected var dead = false
    override fun onOutScreen() {
        dead = true
    }

    override fun isDead(): Boolean {
        return this.dead
    }

    override var onDispose = {}
}

// regular bullet with delta speed movement
abstract class RegularBullet(
    override var x: Double,
    override var y: Double,
    var spdX: Double,
    val spdY: Double,
    width: Double,
    height: Double
) : BaseBullet(width, height) {
    override fun move() {
        this.x += this.spdX
        this.y += this.spdY
    }
}

class HeroBullet(
    val belong: HeroAircraft,
    private val damage: Double,
    x: Double,
    y: Double,
    spdX: Double,
    spdY: Double
) : RegularBullet(x, y, spdX, spdY, width, height) {
    companion object {
        const val width = 12.0
        const val height = 12.0
    }

    override fun hit(other: BaseAircraft) {
        other.addHP(-damage)
        dead = true
    }
}

class EnemyBullet(private val damage: Double, x: Double, y: Double, spdX: Double, spdY: Double) :
    RegularBullet(x, y, spdX, spdY, width, height) {
    companion object {
        const val width = 12.0
        const val height = 12.0
    }

    override fun hit(other: BaseAircraft) {
        other.addHP(-damage)
        dead = true
    }
}
