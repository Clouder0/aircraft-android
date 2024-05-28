package tech.caaa.aircraft.game

sealed class UserInput {
    data class MovePlane(val planeId: UInt, val x: Float, val y: Float) : UserInput()
    data object HoldShoot: UserInput()
}
