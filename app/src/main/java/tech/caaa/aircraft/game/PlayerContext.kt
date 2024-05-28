package tech.caaa.aircraft.game

import tech.caaa.aircraft.aircrafts.HeroAircraft

data class PlayerContext(val controlledHero: HeroAircraft) {
    var score = 0
}
