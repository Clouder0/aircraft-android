package tech.caaa.aircraft.aircrafts

import tech.caaa.aircraft.BoxedCollidable
import tech.caaa.aircraft.common.Disposable
import tech.caaa.aircraft.common.Movable
import kotlin.math.min
import kotlin.math.max


abstract class BaseAircraft(override var x: Float, override var y: Float, width: Float, height: Float, private var maxHP: Float) : Movable, BoxedCollidable(width,height),
    Disposable {
    protected var hp = maxHP
    override fun isDead(): Boolean {  return this.hp <= 0  }

    override fun onDispose() {}
    fun addHP(num: Float) {
        this.hp = max(0F, min(this.hp + num, this.maxHP))
    }
}
