package tech.caaa.aircraft.aircrafts

import tech.caaa.aircraft.bullets.Bullet
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
    override fun shoot() : List<Bullet> {
        return emptyList<Bullet>()
    }
}
