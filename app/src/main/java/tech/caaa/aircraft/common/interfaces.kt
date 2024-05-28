package tech.caaa.aircraft.common

interface Movable {
    var x: Double
    var y: Double
    fun move()
    fun onOutScreen()
}

interface Disposable {
    fun isDead(): Boolean
    fun onDispose()
}