package tech.caaa.aircraft.aircrafts

import tech.caaa.aircraft.aircrafts.shoot.linearShoot
import tech.caaa.aircraft.aircrafts.shoot.singleRegularShootWrap
import tech.caaa.aircraft.aircrafts.shoot.timedShoot
import tech.caaa.aircraft.bullets.BaseBullet
import tech.caaa.aircraft.bullets.HeroBullet
import tech.caaa.aircraft.bullets.Shootable
import tech.caaa.aircraft.common.curry
import kotlin.random.Random
import kotlin.random.nextUInt

const val HeroMaxHP = 1000.0

class HeroAircraft(x: Double, y: Double) : BaseAircraft(x,y, width, height, HeroMaxHP), Shootable {
    companion object {
        const val width = 50.0
        const val height = 41.0
    }
    val planeId = Random.nextUInt()
    public fun setPosition(nx: Double, ny: Double) {
        this.x = nx
        this.y = ny
    }
    override fun move(){
        // do nothing as we will directly set position when frame starts.
    }


    private val shooter = ::HeroBullet.curry(this).curry(10.0).singleRegularShootWrap().linearShoot(-2.0).timedShoot(15)
    @Suppress("UNCHECKED_CAST")
    override fun shoot() : List<HeroBullet> {
        return shooter(x,y) as List<HeroBullet>
    }

    override fun onOutScreen() {
        // do nothing as hero aircraft can be back once user clicks
    }
}
