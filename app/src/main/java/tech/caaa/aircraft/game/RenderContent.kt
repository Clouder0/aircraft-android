package tech.caaa.aircraft.game

import android.graphics.Rect

sealed class Renderable {
    abstract val hitbox: Rect
    class HeroAircraft(override val hitbox: Rect) : Renderable()
    class HeroBullet(override val hitbox: Rect) : Renderable()
    class CommonEnemy(override val hitbox: Rect) : Renderable()
}

enum class Background {
    GRASS,
    SKY,
    MOUNTAIN,
    NIGHT,
    HOT
}

class RenderContent(val players: List<PlayerContext>, val contents: List<Renderable>, val background: Background)
