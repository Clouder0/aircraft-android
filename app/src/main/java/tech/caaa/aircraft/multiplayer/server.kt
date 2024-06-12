package tech.caaa.aircraft.multiplayer

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tech.caaa.aircraft.game.Game
import tech.caaa.aircraft.game.RenderContent
import tech.caaa.aircraft.game.UserInput
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.concurrent.thread

fun serializeToBytes(obj: Serializable): ByteArray {
    ByteArrayOutputStream().use { byteArrayOutputStream ->
        ObjectOutputStream(byteArrayOutputStream).use { objectOutputStream ->
            objectOutputStream.writeObject(obj)
            objectOutputStream.flush()
            return byteArrayOutputStream.toByteArray()
        }
    }
}

fun <T> deserializeFromBytes(bytes: ByteArray): T {
    ByteArrayInputStream(bytes).use { byteArrayInputStream ->
        ObjectInputStream(byteArrayInputStream).use { objectInputStream ->
            @Suppress("UNCHECKED_CAST")
            return objectInputStream.readObject() as T
        }
    }
}

data class PlayerInfo(val address: InetAddress, val port: Int)
data class RegisteredPlayer(val name: String, val id: UInt) : Serializable

class GameServer(private val game: Game, private val port: Int) : Runnable {

    private var socket: DatagramSocket = DatagramSocket(port)
    private var running = false
    private val buffer = ByteArray(128 * 1024) // 128kb buffer
    private val playerList = mutableListOf<PlayerInfo>()
    companion object {
        const val fps = 60
        const val nsPerFrame = (1e9 / fps).toLong()
    }

    fun waitForPlayer() : RegisteredPlayer {
        socket.soTimeout = 0
        val packet = DatagramPacket(buffer, buffer.size)
        socket.receive(packet)
        val playerName = String(packet.data,0,packet.length)
        val ret = game.addPlayer(playerName)
        val addedPlayer = RegisteredPlayer(playerName, ret)
        val retPacketBuffer = serializeToBytes(addedPlayer)
        playerList.add(PlayerInfo(packet.address,packet.port))
        val retPack = DatagramPacket(retPacketBuffer,retPacketBuffer.size, packet.address, packet.port)
        socket.send(retPack)
        return addedPlayer
    }
    override fun run() {
        try {
            thread {
                var nextFrameTime = System.nanoTime()
                var lastRenderContent: RenderContent? = null
                while(running) {
                    val nowTime = System.nanoTime()
                    if(nowTime < nextFrameTime) continue
                    nextFrameTime = nowTime + nsPerFrame
                    var content: RenderContent? = null
                    runBlocking {
                        content = game.getRenderContent()
                    }
                    if(content == null) continue
                    if(content == lastRenderContent) continue
                    lastRenderContent = content
                    val renderBuffer = serializeToBytes(content!!)
                    for(player in playerList) {
                        val packet = DatagramPacket(renderBuffer,renderBuffer.size, player.address,player.port)
                        socket.send(packet)
                    }
                }
            }
            running = true

            while (running) {
                val packet = DatagramPacket(buffer, buffer.size)
                socket.receive(packet)
                val received = deserializeFromBytes<UserInput>(packet.data)
                runBlocking {
                    launch {
                        game.addInput(received)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            socket.close()
        }
    }

    fun stop() {
        running = false
        socket.close()
    }
}
