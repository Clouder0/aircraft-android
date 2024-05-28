package tech.caaa.aircraft.aircrafts.shoot

import android.util.Log
import tech.caaa.aircraft.bullets.BaseBullet

typealias shootFunc = () -> List<BaseBullet>
typealias regularShootFunc = (x: Float, y: Float, spdX: Float, spdY: Float) -> List<BaseBullet>


// shoot per <period> ticks
fun timedShoot(target: Int): (() -> List<BaseBullet>) -> List<BaseBullet> {
    var nowTick = 0
    val res = fun(callback: () -> List<BaseBullet>): List<BaseBullet>{
        ++nowTick
        if(nowTick >= target) {
            nowTick = 0
            return callback()
        }
        return emptyList()
    }
    return res
}
