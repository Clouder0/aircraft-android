package tech.caaa.aircraft.game

import android.graphics.Rect

sealed class Renderable {
    abstract val hitbox: Rect

    class HeroAircraft(override val hitbox: Rect) : Renderable()
    class HeroBullet(override val hitbox: Rect) : Renderable()
    class EnemyBullet(override val hitbox: Rect) : Renderable()
    class CommonEnemy(override val hitbox: Rect) : Renderable()
    class EliteEnemy(override val hitbox: Rect) : Renderable()
    class BossEnemy(override val hitbox: Rect) : Renderable()
    class BloodItem(override val hitbox: Rect) : Renderable()
    class BombItem(override val hitbox: Rect) : Renderable()
    class BulletItem(override val hitbox: Rect) : Renderable()
}

enum class Background {
    GRASS,
    SKY,
    MOUNTAIN,
    NIGHT,
    HOT
}

class PlayerRenderContext(
    val name: String,
    val id: UInt,
    val planeId: UInt,
    val score: Int,
    val hp: Int
)

class RenderContent(
    val players: List<PlayerRenderContext>,
    val contents: List<Renderable>,
    val background: Background
)
