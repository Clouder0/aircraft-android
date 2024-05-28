package tech.caaa.aircraft.aircrafts

import kotlin.random.Random
import kotlin.random.nextUInt


abstract class BaseEnemy(
    x: Float,
    y: Float,
    var spdX: Float,
    var spdY: Float,
    width: Float,
    height: Float,
    maxHP: Float
) : BaseAircraft(x, y, width, height, maxHP) {

    override fun onOutScreen() {
        // die when out of screen
        this.hp = 0F
    }

    override fun move() {
        x += spdX
        y += spdY
    }
}


typealias emptyGenerator = () -> List<BaseEnemy>
typealias positionedGenerator = (x: Float, y: Float) -> List<BaseEnemy>

fun randomTopGenWrapper(maxX: Float, inner: positionedGenerator): emptyGenerator {
    return {
        inner(Random.nextFloat() * maxX,20F)
    }
}

fun chanceGenWrapper(chance: Float, inner: emptyGenerator): emptyGenerator {
    return {
        if (Random.nextFloat() < chance) inner() else emptyList<BaseEnemy>()
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

fun singleGenWrapper(inner: (x: Float, y: Float) -> BaseEnemy): positionedGenerator =
    { x: Float, y: Float -> listOf(inner(x, y)) }

class CommonEnemy(x: Float, y: Float) : BaseEnemy(x, y, 0F, spdY, width, height, maxHP) {
    companion object {
        const val spdY = 2F
        const val maxHP = 10F
        const val width = 32F
        const val height = 32F
    }
}