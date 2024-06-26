package tech.caaa.aircraft.aircrafts

import tech.caaa.aircraft.common.BoxedCollidable
import tech.caaa.aircraft.common.Disposable
import tech.caaa.aircraft.common.Movable
import kotlin.math.max
import kotlin.math.min


abstract class BaseAircraft(
    override var x: Double,
    override var y: Double,
    width: Double,
    height: Double,
    private var maxHP: Double
) : Movable, BoxedCollidable(width, height),
    Disposable {
    var hp = maxHP
    override fun isDead(): Boolean {
        return this.hp <= 0
    }

    override var onDispose = {}
    fun addHP(num: Double) {
        this.hp = max(0.0, min(this.hp + num, this.maxHP))
    }
}
