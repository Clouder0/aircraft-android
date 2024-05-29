package tech.caaa.aircraft.aircrafts.shoot

import android.util.Log
import tech.caaa.aircraft.bullets.BaseBullet
import kotlin.math.cos
import kotlin.math.sin

typealias shootFunc = (x: Double, y: Double) -> List<BaseBullet>
typealias regularShootFunc = (x: Double, y: Double, spdX: Double, spdY: Double) -> List<BaseBullet>
typealias singleRegularShootFunc = (x: Double, y: Double, spdX: Double, spdY: Double) -> BaseBullet


// shoot per <period> ticks
fun shootFunc.timedShoot(target: Int): shootFunc {
    var nowTick = 0
    val res = fun(x: Double, y: Double): List<BaseBullet> {
        ++nowTick
        if (nowTick >= target) {
            nowTick = 0
            return this(x, y)
        }
        return emptyList()
    }
    return res
}

fun regularShootFunc.linearShoot(spdY: Double): shootFunc {
    return fun(x: Double, y: Double): List<BaseBullet> {
        return this(x, y, 0.0, spdY)
    }
}

fun singleRegularShootFunc.singleRegularShootWrap(): regularShootFunc {
    return { x: Double, y: Double, spdX: Double, spdY: Double ->
        listOf(this(x, y, spdX, spdY))
    }
}

// default +y direction, if -y direction, pass negative radius
fun shootFunc.circleShoot(num: Int, radius: Double): shootFunc {
    return fun(x: Double, y: Double): List<BaseBullet> {
        val res = ArrayList<BaseBullet>()
        for (i in 0..<num) {
            val nx = x + radius * cos(Math.PI / (num - 1) * i)
            val ny = y + radius * sin(Math.PI / (num - 1) * i)
            res.addAll(this(nx, ny))
        }
        return res
    }
}