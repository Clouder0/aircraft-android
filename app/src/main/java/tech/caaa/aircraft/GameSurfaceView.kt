package tech.caaa.aircraft

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tech.caaa.aircraft.game.Background
import tech.caaa.aircraft.game.Game
import tech.caaa.aircraft.game.PlayerRenderContext
import tech.caaa.aircraft.game.Rectangle
import tech.caaa.aircraft.game.RenderContent
import tech.caaa.aircraft.game.Renderable
import tech.caaa.aircraft.game.UserInput
import kotlin.concurrent.thread

class GameSurfaceView(context: Context, attrs: AttributeSet) : SurfaceView(context, attrs),
    SurfaceHolder.Callback {

    private var renderThread: Thread? = null
    private var playerId: UInt? = null
    private lateinit var renderGetter: () -> RenderContent?
    private lateinit var inputCallback: (UserInput) -> Unit
    var running = false
    fun init(playerId: UInt, renderGetter: () -> RenderContent?, inputCallback: (UserInput)->Unit) {
        this.playerId = playerId
        this.renderGetter = renderGetter
        this.inputCallback = inputCallback
        if(GlobalCtx.serverGameServer != null) {
            thread {GlobalCtx.serverGameServer!!.run()}
        }
        if(GlobalCtx.clientGameClient != null) {
            thread {GlobalCtx.clientGameClient!!.run()}
        }
        renderThread = thread(start = true) {
            this.renderThread(holder)
        }
    }

    init {
        holder.addCallback(this)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
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
        if(!running) return true
        if (event == null) return true
        inputCallback(
            UserInput.MovePlane(
                playerId!!,
                event.x / wScale,
                event.y / hScale
            )
        )
        return true
    }

    private var lastRendered: RenderContent? = null
    private fun renderThread(holder: SurfaceHolder) {
        while (running) {
            val content = this.renderGetter() ?: continue
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
            renderMyHP(canvas, content.players.find { p -> p.id == playerId }!!)

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
        canvas.drawBitmap(bitmapOfRenderable(r), null, rectangle2Rect(scaleRectangle(r.hitbox)), null)
    }

    private fun rectangle2Rect(r: Rectangle): Rect {
        return Rect(r.left,r.top,r.right,r.bottom)
    }

    private fun scaleRectangle(src: Rectangle): Rectangle {
        return Rectangle(
            (src.left * wScale).toInt(), (src.top * hScale).toInt(),
            (src.right * wScale).toInt(), (src.bottom * hScale).toInt()
        )
    }
}