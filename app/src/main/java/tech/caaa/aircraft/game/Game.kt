package tech.caaa.aircraft.game

import android.graphics.Rect
import android.util.Log
import tech.caaa.aircraft.aircrafts.BaseEnemy
import tech.caaa.aircraft.aircrafts.CommonEnemy
import tech.caaa.aircraft.aircrafts.HeroAircraft
import tech.caaa.aircraft.aircrafts.chanceGenWrapper
import tech.caaa.aircraft.aircrafts.counterGenWrapper
import tech.caaa.aircraft.aircrafts.randomTopGenWrapper
import tech.caaa.aircraft.aircrafts.repeatGenWrapper
import tech.caaa.aircraft.aircrafts.singleGenWrapper
import tech.caaa.aircraft.bullets.BaseBullet
import tech.caaa.aircraft.bullets.HeroBullet
import tech.caaa.aircraft.common.Movable
import java.util.Timer
import java.util.TimerTask
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.timerTask
import kotlin.time.measureTime


enum class Difficulty {
    EASY,
    MEDIUM,
    HARD
}

// use 400 * 800 size and scale at runtime
class Game(private val difficulty: Difficulty) {
    companion object {
        const val baseWidth = 400F
        const val baseHeight = 800F
        const val fps = 60
        const val nsPerFrame = (1e9 / fps).toLong()
    }

    private var renderContent: RenderContent? = null
    private val userInputBuffer = ArrayList<UserInput>()
    private val heroes = ArrayList<HeroAircraft>()
    private val players = ArrayList<PlayerContext>()
    private val enemies = ArrayList<BaseEnemy>()
    private val enemyFactories = ArrayList<() -> List<BaseEnemy>>()
    private val heroBullets = ArrayList<BaseBullet>()
    private val enemyBullets = ArrayList<BaseBullet>()

    fun addPlayer(): PlayerContext {
        val newHero = HeroAircraft(0F, 0F)
        heroes.add(newHero)
        val player = PlayerContext(newHero)
        return player
    }

    init {
        enemyFactories.add(
            repeatGenWrapper(
                5,
                counterGenWrapper(
                    60,
                    chanceGenWrapper(
                        0.5F,
                        randomTopGenWrapper(baseWidth, singleGenWrapper(::CommonEnemy))
                    )
                )
            )
        )
    }

    fun run() {

        var nextFrameTime = System.nanoTime()
        while (true) {
//            val measured = measureTime {
            // spin until time reached
            var nowTime = System.nanoTime()
            while (nowTime < nextFrameTime) {
                nowTime = System.nanoTime()
            }
            // long time accuracy
            nextFrameTime += nsPerFrame
            Log.d("Game", "start game frame")
            once()
//            }
//            Log.d("time", measured.inWholeMicroseconds.toString())
        }
    }

    // execute one game loop
    private fun once() {
        handleInput()
        doMoves()
        detectCollision()
        detectOutScreen()
        cleanObjects()
        generateObjects()
        updateRenderContent()
    }

    private fun detectCollision() {
        for(hero in heroes) {
            if(hero.isDead()) continue
            for(enemy in enemies) {
                if(enemy.isDead()) continue
                if(hero.isDead()) break
                if(!hero.check(enemy)) continue
                enemy.addHP(-1000F) // strong hit enemy
                hero.addHP(-100F) // bad hit hero
            }
        }

        for(enemy in enemies) {
            if(enemy.isDead()) continue
            for(bullet in heroBullets) {
                if(bullet.isDead()) continue
                if(enemy.isDead()) break
                if(!bullet.check(enemy)) continue
                bullet.hit(enemy)
            }
        }

    }

    private fun handleInput() {
        for (input in this.userInputBuffer) {
            when (input) {
                is UserInput.HoldShoot -> {
                }

                is UserInput.MovePlane -> {
                    val target = heroes.find { hero -> hero.planeId == input.planeId } ?: continue
                    target.setPosition(input.x, input.y)
                }
            }
        }
    }

    private fun doMoves() {
        for (bullet in heroBullets) bullet.move()
        for (bullet in enemyBullets) bullet.move()
        for(enemy in enemies) enemy.move()
    }


    private fun checkOutScreen(obj: Movable): Boolean =
        obj.x < 0 || obj.x > baseWidth || obj.y < 0 || obj.y > baseHeight

    private fun detectOutScreen() {
        for (bullet in heroBullets) {
            if (checkOutScreen(bullet)) {
                bullet.onOutScreen()
            }
        }
        for (bullet in enemyBullets) {
            if (checkOutScreen(bullet)) {
                bullet.onOutScreen()
            }
        }
    }

    private fun generateBullets() {
        for (hero in heroes) heroBullets.addAll(hero.shoot())
    }

    private fun generateEnemies() {
        for (fac in enemyFactories) enemies.addAll(fac())
    }

    private fun cleanObjects() {
        userInputBuffer.clear()
        heroBullets.removeIf { bullet -> bullet.isDead() }
        enemyBullets.removeIf { bullet -> bullet.isDead() }
        enemies.removeIf {enemy -> enemy.isDead() }
        Log.d("Game", "hero bullet num ${heroBullets.size}")
    }

    private fun generateObjects() {
        generateBullets()
        generateEnemies()
    }

    private fun updateRenderContent() {
        val content = ArrayList<Renderable>()
        content.addAll(heroes.map { hero -> Renderable.HeroAircraft(hero.hitbox) })
        content.addAll(heroBullets.map { bullet -> Renderable.HeroBullet(bullet.hitbox) })
        content.addAll(enemies.mapNotNull {enemy -> when(enemy){
            is CommonEnemy -> Renderable.CommonEnemy(enemy.hitbox)
            else -> null
        } })
        this.renderContent = RenderContent(
            players = this.players, contents = content, background = when (difficulty) {
                Difficulty.EASY -> Background.GRASS
                Difficulty.MEDIUM -> Background.SKY
                Difficulty.HARD -> Background.HOT
            }
        )
    }

    fun getRenderContent(): RenderContent? {
        return this.renderContent
    }

    fun addInput(input: UserInput) {
        this.userInputBuffer.add(input)
    }
}
