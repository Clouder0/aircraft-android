package tech.caaa.aircraft.common

interface Movable {
    var x: Float
    var y: Float
    fun move()
    fun onOutScreen()
}

interface Disposable {
    fun isDead(): Boolean
    fun onDispose()
}