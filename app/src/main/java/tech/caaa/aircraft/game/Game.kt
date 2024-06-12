package tech.caaa.aircraft.game

import android.util.Log
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import tech.caaa.aircraft.aircrafts.BaseEnemy
import tech.caaa.aircraft.aircrafts.BossEnemy
import tech.caaa.aircraft.aircrafts.CommonEnemy
import tech.caaa.aircraft.aircrafts.EliteEnemy
import tech.caaa.aircraft.aircrafts.HeroAircraft
import tech.caaa.aircraft.aircrafts.chanceGenWrapper
import tech.caaa.aircraft.aircrafts.counterGenWrapper
import tech.caaa.aircraft.aircrafts.fixedYGenWrapper
import tech.caaa.aircraft.aircrafts.incrementWrapper
import tech.caaa.aircraft.aircrafts.randomTopGenWrapper
import tech.caaa.aircraft.aircrafts.repeatGenWrapper
import tech.caaa.aircraft.aircrafts.singleGenWrapper
import tech.caaa.aircraft.bullets.BaseBullet
import tech.caaa.aircraft.bullets.HeroBullet
import tech.caaa.aircraft.bullets.Shootable
import tech.caaa.aircraft.common.Movable
import tech.caaa.aircraft.items.BaseItem
import tech.caaa.aircraft.items.BloodItem
import tech.caaa.aircraft.items.BombItem
import tech.caaa.aircraft.items.BulletItem


enum class Difficulty {
    EASY,
    MEDIUM,
    HARD
}

// use 400 * 800 size and scale at runtime
class Game(private val difficulty: Difficulty, private val audioHelper: MusicHelper?) {
    companion object {
        const val baseWidth = 400.0
        const val baseHeight = 800.0
        const val fps = 60
        const val nsPerFrame = (1e9 / fps).toLong()
    }

    private var frameCnt = 0U
    private var renderContent: RenderContent? = null
    private val inputMutex = Mutex()
    private val userInputBuffer = ArrayList<UserInput>()
    private val userMoveInput = mutableMapOf<UInt, UserInput.MovePlane>()
    private val heroes = ArrayList<HeroAircraft>()
    private val players = ArrayList<PlayerContext>()
    private val enemies = ArrayList<BaseEnemy>()
    private val enemyFactories = ArrayList<() -> List<BaseEnemy>>()
    private val heroBullets = ArrayList<HeroBullet>()
    private val enemyBullets = ArrayList<BaseBullet>()
    private val items = ArrayList<BaseItem>()
    private val eventManager = EventManager()

    fun addPlayer(name: String): UInt {
        val newHero = HeroAircraft(0.0, 0.0)
        heroes.add(newHero)
        val player = PlayerContext(name, newHero)
        players.add(player)
        return player.id
    }

    fun getPlayerScore(id: UInt): Int {
        return players.find { p -> p.id == id }?.score ?: -1
    }

    private val background_music = audioHelper?.playLong(LongMusicResource.BGM)

    init {
        enemyFactories.addAll(
            listOf(
                repeatGenWrapper(
                    when (difficulty) {
                        Difficulty.EASY -> 5
                        Difficulty.MEDIUM -> 8
                        Difficulty.HARD -> 10
                    },
                    counterGenWrapper(
                        60,
                        chanceGenWrapper(
                            0.5,
                            randomTopGenWrapper(baseWidth, singleGenWrapper(::CommonEnemy))
                        )
                    )
                ),
                repeatGenWrapper(
                    when (difficulty) {
                        Difficulty.EASY -> 3
                        Difficulty.MEDIUM -> 5
                        Difficulty.HARD -> 8
                    },
                    counterGenWrapper(
                        180,
                        chanceGenWrapper(
                            0.4,
                            randomTopGenWrapper(baseWidth, singleGenWrapper(::EliteEnemy))
                        )
                    )
                )
            )
        )
    }

    fun run() {

        var nextFrameTime = System.nanoTime()
        while (!gameOver) {
//            val measured = measureTime {
            // spin until time reached
            var nowTime = System.nanoTime()
            while (nowTime < nextFrameTime) {
                nowTime = System.nanoTime()
            }
            // long time accuracy
            nextFrameTime += nsPerFrame
            Log.d("Game", "start game frame")
            ++frameCnt
            once()
//            }
//            Log.d("time", measured.inWholeMicroseconds.toString())
        }
    }


    // execute one game loop
    private fun once() {
        gameOverCheck()
        if (gameOver) return
        runBlocking { handleInput() }
        doMoves()
        detectCollision()
        handleEvents()
        detectOutScreen()
        cleanObjects()
        generateObjects()
        updateRenderContent()
    }

    private fun handleEvents() {
        val events = eventManager.eventsAtFrame(frameCnt)
        for (e in events) {
            e.callback()
            when (e) {
                is Event.HeroEnhanceBulletExpireEvent -> {}
            }
        }
    }

    private fun detectCollision() {
        for (hero in heroes) {
            if (hero.isDead()) continue
            for (enemy in enemies) {
                if (enemy.isDead()) continue
                if (hero.isDead()) break
                if (!hero.check(enemy)) continue
                enemy.addHP(-1000.0) // strong hit enemy
                hero.addHP(-100.0) // bad hit hero
            }
        }

        for (enemy in enemies) {
            if (enemy.isDead()) continue
            for (bullet in heroBullets) {
                if (bullet.isDead()) continue
                if (enemy.isDead()) break
                if (!bullet.check(enemy)) continue
                bullet.hit(enemy)
                audioHelper?.playShort(ShortMusicResource.BULLET_HIT)
                if (enemy.isDead()) {
                    val player =
                        players.find { p -> p.controlledHero.planeId == bullet.belong.planeId }
                    if (player != null) player.score += enemy.getScore()
                    pendingItems.addAll(enemy.genLoot())
                }
            }
        }

        for (hero in heroes) {
            if (hero.isDead()) continue
            for (bullet in enemyBullets) {
                if (bullet.isDead()) continue
                if (hero.isDead()) break
                if (!bullet.check(hero)) continue
                bullet.hit(hero)
                audioHelper?.playShort(ShortMusicResource.BULLET_HIT)
            }
        }

        for (hero in heroes) {
            if (hero.isDead()) continue
            for (item in items) {
                if (item.isDead()) continue
                if (hero.isDead()) break
                if (!item.check(hero)) continue
                val ret = item.use()
                audioHelper?.playShort(ShortMusicResource.GET_SUPPLY)
                when (item) {
                    is BloodItem -> hero.addHP(ret as Double)
                    is BulletItem -> {
                        hero.enhanceShoot()
                        eventManager.registerEvent(Event.HeroEnhanceBulletExpireEvent(frameCnt + 300U) {
                            hero.enhanceShootExpired()
                        })
                    }

                    is BombItem -> {
                        audioHelper?.playShort(ShortMusicResource.BOMB_EXPLOSION)
                        for (enemy in enemies) {
                            if (enemy.isDead()) continue
                            enemy.addHP(-100.0)
                            if (enemy.isDead()) {
                                val p =
                                    players.find { p -> p.controlledHero.planeId == hero.planeId }
                                if (p != null) p.score += enemy.getScore()
                                pendingItems.addAll(enemy.genLoot())
                            }
                        }
                    }
                }
            }
        }
    }

    private suspend fun handleInput() {
        inputMutex.withLock {
            for (input in this.userMoveInput.values) {
                val user = players.find { p -> p.id == input.playerId } ?: continue
                user.controlledHero.setPosition(input.x, input.y)
            }
            for (input in this.userInputBuffer) {
                when (input) {

                    is UserInput.MovePlane -> {
                    }
                }
            }
        }
    }

    private fun doMoves() {
        for (bullet in heroBullets) bullet.move()
        for (bullet in enemyBullets) bullet.move()
        for (enemy in enemies) enemy.move()
    }


    private fun checkOutScreen(obj: Movable): Boolean =
        obj.x < 0 || obj.x > baseWidth || obj.y < 0 || obj.y > baseHeight

    private fun detectOutScreen() {
        for (bullet in heroBullets) if (checkOutScreen(bullet)) bullet.onOutScreen()
        for (bullet in enemyBullets) if (checkOutScreen(bullet)) bullet.onOutScreen()
        for (enemy in enemies) if (checkOutScreen(enemy)) enemy.onOutScreen()
    }

    private fun generateBullets() {
        for (hero in heroes) if(!hero.isDead()) heroBullets.addAll(hero.shoot())
        for (enemy in enemies) if (enemy is Shootable) enemyBullets.addAll(enemy.shoot())
    }

    private val bossFactory =
        fixedYGenWrapper(120.0, baseWidth, singleGenWrapper(incrementWrapper(::BossEnemy)))
    private var lastBossScore = 100
    private var bossAlive = false
    private var bossAudio: UInt? = null
    private fun generateEnemies() {
        for (fac in enemyFactories) enemies.addAll(fac())
        val scoreSum =
            players.fold(0) { acc: Int, playerContext: PlayerContext -> acc + playerContext.score }
        if (!bossAlive && scoreSum >= lastBossScore * 1.5) {
            lastBossScore = scoreSum
            bossAlive = true
            val boss = bossFactory()
            enemies.addAll(boss)
            audioHelper?.pauseLong(background_music!!)
            bossAudio = audioHelper?.playLong(LongMusicResource.BGM_BOSS)
            boss[0].onDispose = {
                this.bossAlive = false
                val nowScoreSum =
                    this.players.fold(0) { acc: Int, playerContext: PlayerContext -> acc + playerContext.score }
                this.lastBossScore = nowScoreSum
                audioHelper?.pauseLong(bossAudio!!)
                audioHelper?.resumeLong(background_music!!)
            }
        }
    }

    private fun cleanObjects() {
        userInputBuffer.clear()
        userMoveInput.clear()

        heroBullets.filter { x -> x.isDead() }.forEach { x -> x.onDispose() }
        enemyBullets.filter { x -> x.isDead() }.forEach { x -> x.onDispose() }
        enemies.filter { x -> x.isDead() }.forEach { x -> x.onDispose() }
        items.filter { x -> x.isDead() }.forEach { x -> x.onDispose() }

        heroBullets.removeIf { bullet -> bullet.isDead() }
        enemyBullets.removeIf { bullet -> bullet.isDead() }
        enemies.removeIf { enemy -> enemy.isDead() }
        items.removeIf { item -> item.isDead() }
        Log.d("Game", "hero bullet num ${heroBullets.size}")
    }

    private val pendingItems = ArrayList<BaseItem>()
    private fun generateObjects() {
        generateBullets()
        generateEnemies()
        items.addAll(pendingItems)
        pendingItems.clear()
    }

    private fun updateRenderContent() {
        val content = ArrayList<Renderable>()
        content.addAll(heroes.map { hero -> Renderable.HeroAircraft(hero.hitbox) })
        content.addAll(heroBullets.map { bullet -> Renderable.HeroBullet(bullet.hitbox) })
        content.addAll(enemyBullets.map { bullet -> Renderable.EnemyBullet(bullet.hitbox) })
        content.addAll(enemies.mapNotNull { enemy ->
            when (enemy) {
                is CommonEnemy -> Renderable.CommonEnemy(enemy.hitbox)
                is EliteEnemy -> Renderable.EliteEnemy(enemy.hitbox)
                is BossEnemy -> Renderable.BossEnemy(enemy.hitbox)
                else -> null
            }
        })
        content.addAll(items.mapNotNull { item ->
            when (item) {
                is BloodItem -> Renderable.BloodItem(item.hitbox)
                is BulletItem -> Renderable.BulletItem(item.hitbox)
                is BombItem -> Renderable.BombItem(item.hitbox)
                else -> null
            }
        })
        runBlocking {
            renderMtx.withLock {
                renderContent = RenderContent(
                    players = players.map { ctx -> makePlayerRenderCtx(ctx) },
                    contents = content,
                    background = when (difficulty) {
                        Difficulty.EASY -> Background.GRASS
                        Difficulty.MEDIUM -> Background.SKY
                        Difficulty.HARD -> Background.HOT
                    }
                )
            }
        }

    }
    private val renderMtx = Mutex()
    suspend fun getRenderContent(): RenderContent? {
        renderMtx.withLock {
            return this.renderContent
        }
    }

    suspend fun addInput(input: UserInput) {
        inputMutex.withLock {
            when (input) {
                is UserInput.MovePlane -> {
                    val old = this.userMoveInput[input.playerId]
                    if (old == null || old.created < input.created) {
                        this.userMoveInput[input.playerId] = input
                    }
                }

                else -> this.userInputBuffer.add(input)
            }
            Unit
        }
    }

    private var gameOver = false
    private val onGameOver = ArrayList<() -> Unit>()
    private fun gameOverCheck() {
        if (gameOver) return
        if (players.all { p -> p.controlledHero.isDead() }) {
            // game over! damn it!
            gameOver = true
            onGameOver.forEach { x -> x() }
            audioHelper?.pauseLong(background_music!!)
            audioHelper?.playShort(ShortMusicResource.GAME_OVER)
        }
    }

    fun registerOnGameOver(callback: () -> Unit) {
        this.onGameOver.add(callback)
    }
}
