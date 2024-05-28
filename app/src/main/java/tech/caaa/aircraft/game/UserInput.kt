package tech.caaa.aircraft.game

sealed class UserInput {
    data class MovePlane(val planeId: UInt, val x: Double, val y: Double) : UserInput()
    data object HoldShoot: UserInput()
}
