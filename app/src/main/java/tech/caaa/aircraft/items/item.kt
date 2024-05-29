package tech.caaa.aircraft.items

import tech.caaa.aircraft.common.BoxedCollidable
import tech.caaa.aircraft.common.Disposable

abstract class BaseItem(
    override val x: Double,
    override val y: Double,
    width: Double,
    height: Double
) : BoxedCollidable(width, height), Disposable {
    private var used: Boolean = false
    open fun use(): Any {
        used = true
        return Unit
    }

    override fun isDead(): Boolean {
        return used
    }

    override fun onDispose() {}
}

class BloodItem(x: Double, y: Double) : BaseItem(x, y, width, height) {
    companion object {
        const val width = 16.0
        const val height = 16.0
        const val healHP = 50.0
    }

    override fun use(): Any {
        super.use()
        return healHP
    }

}

class BombItem(x: Double, y: Double) : BaseItem(x, y, width, height) {
    companion object {
        const val width = 16.0
        const val height = 16.0
    }
}

class BulletItem(x: Double, y: Double) : BaseItem(x, y, width, height) {
    companion object {
        const val width = 16.0
        const val height = 16.0
    }
}