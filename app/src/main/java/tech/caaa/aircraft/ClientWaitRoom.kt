package tech.caaa.aircraft

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.runBlocking
import tech.caaa.aircraft.multiplayer.GameClient
import kotlin.concurrent.thread

class ClientWaitRoom : AppCompatActivity() {
    private val mainThreadHandler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_client_wait_room)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val textEdit: EditText = findViewById(R.id.editText)
        val enterBtn: Button = findViewById(R.id.enterBtn)
        enterBtn.setOnClickListener {
            thread {
                val client = GameClient(null, textEdit.text.toString(), 11451)
                val id = client.registerPlayer(GlobalCtx.username)
                GlobalCtx.clientGameClient = client
                GlobalCtx.clientControlledId = id
                mainThreadHandler.post {
                    Thread.sleep(1000)
                    Toast.makeText(this, "Connected!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, ClientGameActivity::class.java)
                    startActivity(intent)
                }
            }

        }
    }
}