package tech.caaa.aircraft.game

import java.io.Serializable

sealed class UserInput : Serializable {
    data class MovePlane(val playerId: UInt, val x: Double, val y: Double) : UserInput()

    val created = System.nanoTime()
}
