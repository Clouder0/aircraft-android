package tech.caaa.aircraft.game

import tech.caaa.aircraft.aircrafts.HeroAircraft
import kotlin.random.Random
import kotlin.random.nextUInt

data class PlayerContext(val name: String, val controlledHero: HeroAircraft) {
    var score = 0
    val id = Random.nextUInt()
}
