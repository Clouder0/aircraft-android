package tech.caaa.aircraft.game

import android.graphics.Rect
import java.io.Serializable


data class Rectangle(val left: Int, val top: Int, val right: Int, val bottom: Int) : Serializable {
    fun intersects(other: Rectangle): Boolean {
        return if (this.right < other.left || other.right < this.left) {
            false
        } else if (this.bottom < other.top || other.bottom < this.top) {
            false
        } else {
            true
        }
    }
}

sealed class Renderable : Serializable {
    abstract val hitbox: Rectangle

    class HeroAircraft(override val hitbox: Rectangle) : Renderable()
    class HeroBullet(override val hitbox: Rectangle) : Renderable()
    class EnemyBullet(override val hitbox: Rectangle) : Renderable()
    class CommonEnemy(override val hitbox: Rectangle) : Renderable()
    class EliteEnemy(override val hitbox: Rectangle) : Renderable()
    class BossEnemy(override val hitbox: Rectangle) : Renderable()
    class BloodItem(override val hitbox: Rectangle) : Renderable()
    class BombItem(override val hitbox: Rectangle) : Renderable()
    class BulletItem(override val hitbox: Rectangle) : Renderable()
}

enum class Background {
    GRASS,
    SKY,
    MOUNTAIN,
    NIGHT,
    HOT
}

data class PlayerRenderContext(
    val name: String,
    val id: UInt,
    val planeId: UInt,
    val score: Int,
    val hp: Int
) : Serializable

data class RenderContent(
    val players: List<PlayerRenderContext>,
    val contents: List<Renderable>,
    val background: Background
) : Serializable
