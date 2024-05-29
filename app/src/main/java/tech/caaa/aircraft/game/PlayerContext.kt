package tech.caaa.aircraft.game

import tech.caaa.aircraft.aircrafts.HeroAircraft
import kotlin.random.Random
import kotlin.random.nextUInt

data class PlayerContext(val name: String, val controlledHero: HeroAircraft) {
    var score = 0
    val id = Random.nextUInt()
}



fun makePlayerRenderCtx(ctx: PlayerContext):PlayerRenderContext = PlayerRenderContext(ctx.name, ctx.id, ctx.controlledHero.planeId, ctx.score, ctx.controlledHero.hp.toInt())
