package tech.caaa.aircraft.game

import android.graphics.Rect

sealed class Renderable {
    class HeroAircraft(val hitbox: Rect) : Renderable()
}

enum class Background {
    GRASS,
    SKY,
    MOUNTAIN,
    NIGHT,
    HOT
}

class RenderContent(val heroes: List<Renderable.HeroAircraft>, val players: List<PlayerContext>, val background: Background)
