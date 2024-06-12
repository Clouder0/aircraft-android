package tech.caaa.aircraft

import android.app.Activity
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tech.caaa.aircraft.game.Game
import kotlin.concurrent.thread


class GameActivity : AppCompatActivity() {
    private var gameThread: Thread? = null
    private lateinit var gameInstance: Game
    private lateinit var soundPool: SoundPool
    private var controlledPlayerId: UInt? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var audioHelper: AndroidAudio? = null
        if (GlobalCtx.misc_music_enabled) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build()
            val createMediaPlayer = { x: Int -> MediaPlayer.create(this, x) }
            audioHelper = AndroidAudio(createMediaPlayer, soundPool) { x: Int ->
                soundPool.load(
                    this,
                    x,
                    1
                )
            }
        }

        gameInstance = Game(GlobalCtx.difficulty, audioHelper)
        controlledPlayerId = gameInstance.addPlayer(GlobalCtx.username)
        this.gameInstance.registerOnGameOver {
            Handler(Looper.getMainLooper()).post {
                Handler(Looper.getMainLooper()).post {
                    val intent = Intent(this, EndActivity::class.java)
                    intent.putExtra("score", gameInstance.getPlayerScore(controlledPlayerId!!))
                    this.startActivity(intent)
                    (this as Activity).finish()  // Finish the current activity
                }
            }
        }
        gameThread = thread(start = true) { this.gameInstance.run() }
        val gameView: GameSurfaceView = findViewById(R.id.gameView)
        gameView.init(controlledPlayerId!!, {runBlocking{gameInstance.getRenderContent()}}, {x -> runBlocking { launch{gameInstance.addInput(x)} }})
        gameView.running = true
    }
}