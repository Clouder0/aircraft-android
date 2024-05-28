package tech.caaa.aircraft.aircrafts

import tech.caaa.aircraft.aircrafts.shoot.timedShoot
import tech.caaa.aircraft.bullets.BaseBullet
import tech.caaa.aircraft.bullets.HeroBullet
import tech.caaa.aircraft.bullets.Shootable
import kotlin.random.Random
import kotlin.random.nextUInt

const val HeroMaxHP = 1000F

class HeroAircraft(x: Float, y: Float) : BaseAircraft(x,y, width, height, HeroMaxHP), Shootable {
    companion object {
        const val width = 50F
        const val height = 41F
    }
    val planeId = Random.nextUInt()
    public fun setPosition(nx: Float, ny: Float) {
        this.x = nx
        this.y = ny
    }
    override fun move(){
        // do nothing as we will directly set position when frame starts.
    }


    private val shooter = timedShoot(15)
    override fun shoot() : List<BaseBullet> {
        return shooter() {
            listOf(HeroBullet(10F,x,y,0F,-4F))
        }
    }

    override fun onOutScreen() {
        // do nothing as hero aircraft can be back once user clicks
    }
}
