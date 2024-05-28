package tech.caaa.aircraft.aircrafts

import tech.caaa.aircraft.BoxedCollidable
import kotlin.math.min
import kotlin.math.max


interface Movable {
    fun move()
}

abstract class BaseAircraft(override var x: Float, override var y: Float, width: Float, height: Float, private var maxHP: Float) : Movable, BoxedCollidable(width,height) {
    private var hp = maxHP
    val isDead: Boolean
        get() {
            return hp <= 0
        }
    fun addHP(num: Float) {
        this.hp = max(0F, min(this.hp + num, this.maxHP))
    }
}
