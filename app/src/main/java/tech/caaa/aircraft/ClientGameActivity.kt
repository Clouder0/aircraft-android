package tech.caaa.aircraft

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import tech.caaa.aircraft.game.RenderContent
import tech.caaa.aircraft.game.UserInput
import kotlin.concurrent.thread

class ClientGameActivity : AppCompatActivity() {
    private var renderContent: RenderContent? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val gameView: GameSurfaceView = findViewById(R.id.clientGameView)
        GlobalCtx.clientGameClient!!.renderCallback = { content ->
            runBlocking { launch{setRenderContent(content)} }
        }
        gameView.init(GlobalCtx.clientControlledId!!, { renderContent }, {x ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    GlobalCtx.clientGameClient!!.userInput(x)
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        // Handle the exception or show error message
                        Log.e("MainActivity", "Network error: ${e.message}")
                    }
                }
            }})
        gameView.running = true
    }
    private val lk = Mutex()
    private suspend fun setRenderContent(content: RenderContent) {
        lk.withLock {
            renderContent = content
        }
    }
}