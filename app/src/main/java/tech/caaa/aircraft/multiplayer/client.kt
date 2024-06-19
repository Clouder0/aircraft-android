package tech.caaa.aircraft.multiplayer

import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import tech.caaa.aircraft.game.RenderContent
import tech.caaa.aircraft.game.UserInput
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class GameClient(
    var renderCallback: ((RenderContent) -> Unit)?,
    var finishCallback: ((win: Boolean, your_score: Int, other_score: Int) -> Unit)? = null,
    private val remote: String,
    private val port: Int
) {
    private var running = false
    private var socket: DatagramSocket = DatagramSocket()
    var controlledPlayerId: UInt? = null
    private fun sendUdpMessage(buffer: ByteArray, address: String, port: Int) {
        val inetAddress = InetAddress.getByName(address)
        val packet = DatagramPacket(buffer, buffer.size, inetAddress, port)
        // Send packet
        socket.send(packet)
    }

    private val renderBuffer = ByteArray(1024 * 1024)

    fun registerPlayer(name: String) : UInt{
        val nameBuffer = name.toByteArray()
        sendUdpMessage(nameBuffer, remote, port)
        val ret = DatagramPacket(renderBuffer,renderBuffer.size)
        socket.receive(ret)
        val addedPlayer = deserializeFromBytes<RegisteredPlayer>(ret.data)
        if(addedPlayer.name != name) {
            throw Exception("Name Wrong!")
        }
        controlledPlayerId = addedPlayer.id
        return controlledPlayerId!!
    }

    fun run() {
        running = true
        try {
            while (running) {
                val packet = DatagramPacket(renderBuffer, renderBuffer.size)
                socket.receive(packet)
                val renderContent = deserializeFromBytes<RenderContent>(packet.data)
                renderCallback?.invoke(renderContent)
                if(renderContent.players.all { it.hp <= 0 }) {
                    val your_score = renderContent.players.find { it.id == controlledPlayerId }?.score ?: 0
                    val other_score = renderContent.players.find { it.id != controlledPlayerId }?.score ?: 0
                    finishCallback?.invoke(your_score > other_score, your_score, other_score)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            socket.close()
        }
    }

    private val lk = Mutex()
    suspend fun userInput(input: UserInput) {
        lk.withLock {
            sendUdpMessage(serializeToBytes(input), remote, port)
        }
    }

    fun stop() {
        running = false
        socket.close()
    }
}