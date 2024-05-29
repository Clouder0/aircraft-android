package tech.caaa.aircraft

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tech.caaa.aircraft.game.Background
import tech.caaa.aircraft.game.Game
import tech.caaa.aircraft.game.PlayerRenderContext
import tech.caaa.aircraft.game.RenderContent
import tech.caaa.aircraft.game.Renderable
import tech.caaa.aircraft.game.UserInput
import kotlin.concurrent.thread

class GameSurfaceView(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs),
    SurfaceHolder.Callback {

    private var gameThread: Thread? = null
    private var renderThread: Thread? = null
    private val gameInstance = Game(GlobalCtx.difficulty)
    private val controlledPlayerId = gameInstance.addPlayer("me")

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        gameThread = thread(start = true) { this.gameInstance.run() }
        renderThread = thread(start = true) {
            val source = { this.gameInstance.getRenderContent() }
            this.renderThread(holder, source)
        }
    }

    var wScale = width / Game.baseWidth
    var hScale = height / Game.baseHeight
    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        wScale = width / Game.baseWidth
        hScale = height / Game.baseHeight
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        holder.surface.release()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return true
        runBlocking<Unit> {
            launch {
                gameInstance.addInput(
                    UserInput.MovePlane(
                        controlledPlayerId,
                        event.x / wScale,
                        event.y / hScale
                    )
                )
            }
        }
        return true
    }

    private var lastRendered: RenderContent? = null
    private fun renderThread(holder: SurfaceHolder, contentSource: () -> RenderContent?) {
        while (true) {
            val content = contentSource() ?: continue
            if (lastRendered?.equals(content) == true) continue
            lastRendered = content
            renderOnce(holder, content)
        }
    }

    private fun renderOnce(holder: SurfaceHolder, content: RenderContent) {
        var canvas: Canvas? = null
        try {
            canvas = holder.lockCanvas() ?: return

            renderBackground(canvas, content.background)
            for (obj in content.contents) {
                drawRenderable(canvas, obj)
            }
            renderScores(canvas, content.players)
            renderMyHP(canvas, content.players.find { p -> p.id == controlledPlayerId }!!)

        } finally {
            if (canvas != null)
                holder.unlockCanvasAndPost(canvas)
        }
    }

    private var loadedBackground: Background? = null
    private var backgroundBitmap: Bitmap? = null
    private fun renderBackground(canvas: Canvas, background: Background) {
        if (loadedBackground != background) {
            loadedBackground = background
            val bgResource = when (background) {
                Background.GRASS -> R.drawable.bg
                Background.SKY -> R.drawable.bg2
                Background.MOUNTAIN -> R.drawable.bg3
                Background.NIGHT -> R.drawable.bg4
                Background.HOT -> R.drawable.bg5
            }
            backgroundBitmap = BitmapFactory.decodeResource(resources, bgResource)
        }
        canvas.drawBitmap(backgroundBitmap!!, null, Rect(0, 0, width, height), null)
    }

    private fun renderScores(canvas: Canvas, allCtx: List<PlayerRenderContext>) {
        val p = Paint().apply { color = Color.WHITE; textSize = (32.0 * hScale).toFloat() }
        canvas.drawText("Score", (20 * wScale).toFloat(), (40 * hScale).toFloat(), p)
        val ps = Paint().apply { color = Color.WHITE; textSize = (24.0 * hScale).toFloat() }
        allCtx.forEachIndexed { index, ctx ->
            canvas.drawText(
                "${ctx.name}: ${ctx.score}", (20 * wScale).toFloat(),
                ((80 + index * 28) * hScale).toFloat(), ps
            )
        }
    }

    private fun renderMyHP(canvas: Canvas, ctx: PlayerRenderContext) {
        val p = Paint().apply { color = Color.WHITE; textSize = (24.0 * hScale).toFloat() }
        canvas.drawText(
            "HP:${ctx.hp}",
            width - (120 * wScale).toFloat(),
            (28 * hScale).toFloat(),
            p
        )
    }

    private fun idOfRenderable(r: Renderable): Int {
        return when (r) {
            is Renderable.CommonEnemy -> R.drawable.mob
            is Renderable.EliteEnemy -> R.drawable.elite
            is Renderable.BossEnemy -> R.drawable.boss
            is Renderable.HeroAircraft -> R.drawable.hero
            is Renderable.HeroBullet -> R.drawable.bullet_hero
            is Renderable.EnemyBullet -> R.drawable.bullet_enemy
            is Renderable.BloodItem -> R.drawable.prop_blood
            is Renderable.BombItem -> R.drawable.prop_bomb
            is Renderable.BulletItem -> R.drawable.prop_bullet
        }
    }

    private val resourceMap = mutableMapOf<Int, Bitmap>()
    private fun bitmapOfRenderable(r: Renderable): Bitmap {
        val id = idOfRenderable(r)
        val now = resourceMap[id]
        if (now == null) {
            val res = BitmapFactory.decodeResource(resources, id)
            resourceMap[id] = res
            return res
        }
        return now
    }

    private fun drawRenderable(canvas: Canvas, r: Renderable) {
        canvas.drawBitmap(bitmapOfRenderable(r), null, scaleRect(r.hitbox), null)
    }

    private fun scaleRect(src: Rect): Rect {
        return Rect(
            (src.left * wScale).toInt(), (src.top * hScale).toInt(),
            (src.right * wScale).toInt(), (src.bottom * hScale).toInt()
        )
    }
}