package tech.caaa.aircraft

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings.Global
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread

class ServerGameActivity : AppCompatActivity() {
    private lateinit var gameThread: Thread
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_server_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val gameInstance = GlobalCtx.serverGameInstance!!
        gameInstance.registerOnGameOver {
            Handler(Looper.getMainLooper()).post {
                Handler(Looper.getMainLooper()).post {
                    GlobalCtx.serverGameServer!!.stop()
                    val intent = Intent(this, EndActivity::class.java)
                    intent.putExtra("score", gameInstance.getPlayerScore(GlobalCtx.serverControlledPlayer!!))
                    intent.putExtra("score_other", gameInstance.getPlayerScore(GlobalCtx.serverOtherControlledPlayer!!))
                    this.startActivity(intent)
                    (this as Activity).finish()  // Finish the current activity
                }
            }
        }
        gameThread = thread(start = true) { gameInstance.run() }
        val gameView: GameSurfaceView = findViewById(R.id.serverGameView)
        gameView.init(GlobalCtx.serverControlledPlayer!!, { runBlocking{gameInstance.getRenderContent()}}, { x -> runBlocking { launch{ gameInstance.addInput(x) } } })
        gameView.running = true
    }
}