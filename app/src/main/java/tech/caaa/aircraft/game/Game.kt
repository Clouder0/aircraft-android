package tech.caaa.aircraft.game

import android.view.SurfaceHolder
import tech.caaa.aircraft.GameSurfaceView
import tech.caaa.aircraft.aircrafts.HeroAircraft


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
    }

    private var renderContent: RenderContent? = null
    private val userInputBuffer = ArrayList<UserInput>()
    private val heroes = ArrayList<HeroAircraft>()
    private val players = ArrayList<PlayerContext>()

    fun addPlayer() : PlayerContext {
        val newHero = HeroAircraft(0F, 0F)
        heroes.add(newHero)
        val player = PlayerContext(newHero)
        return player
    }

    fun run() {
        while(true) {
            once()
        }
    }

    // execute one game loop
    private fun once() {
        handleInput()
        clear()
        updateRenderContent()
    }

    private fun handleInput() {
        for(input in this.userInputBuffer) {
            when(input) {
                is UserInput.HoldShoot -> {
                }
                is UserInput.MovePlane -> {
                    val target = heroes.find {hero -> hero.planeId == input.planeId} ?: continue
                    target.setPosition(input.x, input.y)
                }
            }
        }
    }

    private fun clear() {
        userInputBuffer.clear()
    }

    private fun updateRenderContent() {
        val renderHeroes = heroes.map { hero -> Renderable.HeroAircraft(hero.hitbox)}
        this.renderContent = RenderContent(renderHeroes, this.players, when(difficulty) {
            Difficulty.EASY -> Background.GRASS
            Difficulty.MEDIUM -> Background.SKY
            Difficulty.HARD -> Background.HOT
        })
    }

    fun getRenderContent(): RenderContent? {
        return this.renderContent
    }

    public fun addInput(input: UserInput) {
        this.userInputBuffer.add(input)
    }
}
