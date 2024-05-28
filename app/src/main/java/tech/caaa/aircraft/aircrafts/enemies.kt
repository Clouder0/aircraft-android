package tech.caaa.aircraft.aircrafts

import tech.caaa.aircraft.items.BaseItem
import tech.caaa.aircraft.items.BloodItem
import tech.caaa.aircraft.items.singleWrapper
import kotlin.random.Random
import kotlin.random.nextUInt


abstract class BaseEnemy(
    x: Double,
    y: Double,
    var spdX: Double,
    var spdY: Double,
    width: Double,
    height: Double,
    maxHP: Double
) : BaseAircraft(x, y, width, height, maxHP) {

    override fun onOutScreen() {
        // die when out of screen
        this.hp = 0.0
    }

    override fun move() {
        x += spdX
        y += spdY
    }
    abstract fun genLoot(): List<BaseItem>
    abstract  fun getScore(): Int
}


typealias emptyGenerator = () -> List<BaseEnemy>
typealias positionedGenerator = (x: Double, y: Double) -> List<BaseEnemy>

fun randomTopGenWrapper(maxX: Double, inner: positionedGenerator): emptyGenerator {
    return {
        inner(Random.nextDouble() * maxX,20.0)
    }
}

fun chanceGenWrapper(chance: Double, inner: emptyGenerator): emptyGenerator {
    return {
        if (Random.nextDouble() < chance) inner() else emptyList<BaseEnemy>()
    }
}

fun repeatGenWrapper(times: Int, inner: emptyGenerator): emptyGenerator {
    return fun(): List<BaseEnemy> {
        val res = ArrayList<BaseEnemy>()
        repeat(times) {
            res.addAll(inner())
        }
        return res
    }
}

fun counterGenWrapper(times: Int, inner: emptyGenerator): emptyGenerator {
    var now = 0
    return fun(): List<BaseEnemy> {
        now++
        if (now < times) return emptyList()
        now = 0
        return inner()
    }
}

fun singleGenWrapper(inner: (x: Double, y: Double) -> BaseEnemy): positionedGenerator =
    { x: Double, y: Double -> listOf(inner(x, y)) }

class CommonEnemy(x: Double, y: Double) : BaseEnemy(x, y, 0.0, spdY, width, height, maxHP) {
    companion object {
        const val spdY = 2.0
        const val maxHP = 10.0
        const val width = 32.0
        const val height = 32.0
    }

    override fun getScore(): Int = 10
    private val looter = tech.caaa.aircraft.items.chanceGenWrapper(0.5, singleWrapper(::BloodItem))
    override fun genLoot():List<BaseItem> {
        return looter(x,y)
    }
}