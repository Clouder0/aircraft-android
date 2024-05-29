package tech.caaa.aircraft.aircrafts

import tech.caaa.aircraft.aircrafts.shoot.circleShoot
import tech.caaa.aircraft.aircrafts.shoot.linearShoot
import tech.caaa.aircraft.aircrafts.shoot.singleRegularShootWrap
import tech.caaa.aircraft.aircrafts.shoot.timedShoot
import tech.caaa.aircraft.bullets.HeroBullet
import tech.caaa.aircraft.bullets.Shootable
import tech.caaa.aircraft.common.curry
import kotlin.random.Random
import kotlin.random.nextUInt

const val HeroMaxHP = 1000.0

class HeroAircraft(x: Double, y: Double) : BaseAircraft(x, y, width, height, HeroMaxHP), Shootable {
    companion object {
        const val width = 50.0
        const val height = 41.0
    }

    val planeId = Random.nextUInt()
    fun setPosition(nx: Double, ny: Double) {
        this.x = nx
        this.y = ny
    }

    override fun move() {
        // do nothing as we will directly set position when frame starts.
    }


    private val linearShooter =
        ::HeroBullet.curry(this).curry(10.0).singleRegularShootWrap().linearShoot(-3.0)
    private val defaultShooter = linearShooter.timedShoot(20)
    private var shooter = defaultShooter

    @Suppress("UNCHECKED_CAST")
    override fun shoot(): List<HeroBullet> {
        return shooter(x, y) as List<HeroBullet>
    }

    sealed class EnhanceLevel {
        object ORIGIN : EnhanceLevel()
        object FASTER : EnhanceLevel()
        class CIRCLE(val level: Int) : EnhanceLevel()
    }

    private var currentEnhanceLevel: EnhanceLevel = EnhanceLevel.ORIGIN
    fun enhanceShoot() {
        currentEnhanceLevel = when (currentEnhanceLevel) {
            EnhanceLevel.ORIGIN -> EnhanceLevel.FASTER
            EnhanceLevel.FASTER -> EnhanceLevel.CIRCLE(1)
            is EnhanceLevel.CIRCLE -> EnhanceLevel.CIRCLE((currentEnhanceLevel as EnhanceLevel.CIRCLE).level + 1)
        }
        updateShootStrategy()
    }

    fun enhanceShootExpired() {
        currentEnhanceLevel = when (currentEnhanceLevel) {
            EnhanceLevel.ORIGIN -> EnhanceLevel.ORIGIN
            EnhanceLevel.FASTER -> EnhanceLevel.ORIGIN
            is EnhanceLevel.CIRCLE -> if ((currentEnhanceLevel as EnhanceLevel.CIRCLE).level <= 1) {
                EnhanceLevel.FASTER
            } else {
                EnhanceLevel.CIRCLE((currentEnhanceLevel as EnhanceLevel.CIRCLE).level - 1)
            }
        }
        updateShootStrategy()
    }

    private fun updateShootStrategy() {
        when (currentEnhanceLevel) {
            EnhanceLevel.ORIGIN -> {
                shooter = defaultShooter
            }

            EnhanceLevel.FASTER -> {
                shooter = linearShooter.timedShoot(5)
            }

            is EnhanceLevel.CIRCLE -> {
                shooter = linearShooter.circleShoot(
                    5 * (currentEnhanceLevel as EnhanceLevel.CIRCLE).level,
                    -(48.0 + 20 * (currentEnhanceLevel as EnhanceLevel.CIRCLE).level)
                ).timedShoot(20)
            }
        }
    }

    override fun onOutScreen() {
        // do nothing as hero aircraft can be back once user clicks
    }
}
