package tech.caaa.aircraft

import android.annotation.SuppressLint
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import tech.caaa.aircraft.game.Game
import tech.caaa.aircraft.multiplayer.GameServer
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections
import kotlin.concurrent.thread

class ServerWaitRoom : AppCompatActivity() {
    private val mainThreadHandler = Handler(Looper.getMainLooper())

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_server_wait_room)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val displayView: TextView = findViewById(R.id.displayView)
        displayView.text = "Waiting for player...\nCurrent addr:" + getDeviceIpAddress() + ":11451"
        thread {
            var audioHelper: AndroidAudio? = null
            if (GlobalCtx.misc_music_enabled) {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val soundPool = SoundPool.Builder()
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

            val gameInstance = Game(GlobalCtx.difficulty, audioHelper)
            val controlledId = gameInstance.addPlayer(GlobalCtx.username)
            val gameServer = GameServer(gameInstance, 11451)
            val otherId = gameServer.waitForPlayer()
            GlobalCtx.serverGameInstance = gameInstance
            GlobalCtx.serverControlledPlayer = controlledId
            GlobalCtx.serverGameServer = gameServer
            GlobalCtx.serverOtherControlledPlayer = otherId.id
            mainThreadHandler.post {
                displayView.text = "Player " + otherId.name + " added."
                Thread.sleep(1000)
                val intent = Intent(this, ServerGameActivity::class.java)
                startActivity(intent)
            }
        }
    }
    private fun getDeviceIpAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (networkInterface in Collections.list(interfaces)) {
                val addresses = networkInterface.inetAddresses
                for (inetAddress in Collections.list(addresses)) {
                    if (!inetAddress.isLoopbackAddress) {
                        val hostAddress = inetAddress.hostAddress
                        // Filter out IPv6 addresses
                        if (inetAddress is InetAddress && hostAddress!!.indexOf(':') < 0) {
                            return hostAddress
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "Unable to get IP Address"
    }
}