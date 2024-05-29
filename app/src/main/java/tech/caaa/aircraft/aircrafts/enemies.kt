package tech.caaa.aircraft.aircrafts

import tech.caaa.aircraft.aircrafts.shoot.linearShoot
import tech.caaa.aircraft.aircrafts.shoot.singleRegularShootWrap
import tech.caaa.aircraft.aircrafts.shoot.timedShoot
import tech.caaa.aircraft.bullets.BaseBullet
import tech.caaa.aircraft.bullets.EnemyBullet
import tech.caaa.aircraft.bullets.Shootable
import tech.caaa.aircraft.common.curry
import tech.caaa.aircraft.items.BaseItem
import tech.caaa.aircraft.items.BloodItem
import tech.caaa.aircraft.items.BombItem
import tech.caaa.aircraft.items.BulletItem
import tech.caaa.aircraft.items.chanceGenWrapper
import tech.caaa.aircraft.items.combineGens
import tech.caaa.aircraft.items.radiusLootGen
import tech.caaa.aircraft.items.singleWrapper
import kotlin.random.Random


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
    abstract fun getScore(): Int
}


typealias emptyGenerator = () -> List<BaseEnemy>
typealias positionedGenerator = (x: Double, y: Double) -> List<BaseEnemy>

fun randomTopGenWrapper(maxX: Double, inner: positionedGenerator): emptyGenerator {
    return {
        inner(Random.nextDouble() * maxX, 20.0)
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
    private val looter = chanceGenWrapper(0.2, singleWrapper(::BloodItem))
    override fun genLoot(): List<BaseItem> {
        return looter(x, y)
    }
}

class EliteEnemy(x: Double, y: Double) : BaseEnemy(x, y, 0.0, spdY, width, height, maxHP),
    Shootable {
    companion object {
        const val spdY = 1.5
        const val maxHP = 20.0
        const val width = 48.0
        const val height = 48.0
    }

    override fun getScore(): Int = 30
    private val looter = combineGens(
        chanceGenWrapper(0.5, radiusLootGen(48.0, singleWrapper(::BloodItem))),
        chanceGenWrapper(0.5, radiusLootGen(48.0, singleWrapper(::BombItem))),
        chanceGenWrapper(0.8, radiusLootGen(48.0, singleWrapper(::BulletItem)))
    )

    override fun genLoot(): List<BaseItem> {
        return looter(x, y)
    }

    private val shooter =
        ::EnemyBullet.curry(10.0).singleRegularShootWrap().linearShoot(spdY + 1.0).timedShoot(60)

    override fun shoot(): List<BaseBullet> {
        return shooter(x, y)
    }
}