package tech.caaa.aircraft

import tech.caaa.aircraft.game.Difficulty
import tech.caaa.aircraft.game.Game
import tech.caaa.aircraft.multiplayer.GameClient
import tech.caaa.aircraft.multiplayer.GameServer

object GlobalCtx {
    var misc_music_enabled = false
    var difficulty = Difficulty.EASY
    var username = "Clouder"
    var serverGameInstance: Game? = null
    var serverControlledPlayer: UInt? = null
    var serverOtherControlledPlayer: UInt? = null
    var serverGameServer: GameServer? = null
    var clientGameClient: GameClient? = null
    var clientControlledId: UInt? = null
}
