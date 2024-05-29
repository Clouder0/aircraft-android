package tech.caaa.aircraft.game

sealed class UserInput {
    data class MovePlane(val playerId: UInt, val x: Double, val y: Double) : UserInput()
    data object HoldShoot: UserInput()
    val created = System.nanoTime()
}
