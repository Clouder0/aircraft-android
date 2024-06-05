package tech.caaa.aircraft

import android.media.MediaPlayer
import android.media.SoundPool
import tech.caaa.aircraft.game.LongMusicResource
import tech.caaa.aircraft.game.MusicHelper
import tech.caaa.aircraft.game.ShortMusicResource

class AndroidAudio(
    val createMediaPlayer: (Int) -> MediaPlayer,
    private val soundPool: SoundPool,
    val poolLoad: (Int) -> Int
) :
    MusicHelper {
    private fun mapLongMusicResource(music: LongMusicResource): Int {
        return when (music) {
            LongMusicResource.BGM -> R.raw.bgm
            LongMusicResource.BGM_BOSS -> R.raw.bgm_boss
        }
    }


    private val shortMusicMap = listOf(
        ShortMusicResource.GET_SUPPLY,
        ShortMusicResource.GAME_OVER,
        ShortMusicResource.BULLET_HIT,
        ShortMusicResource.BOMB_EXPLOSION
    ).associateWith(::mapShortMusicResource).mapValues { entry -> poolLoad(entry.value) }

    private fun mapShortMusicResource(music: ShortMusicResource): Int {
        return when (music) {
            ShortMusicResource.GET_SUPPLY -> R.raw.get_supply
            ShortMusicResource.GAME_OVER -> R.raw.game_over
            ShortMusicResource.BULLET_HIT -> R.raw.bullet_hit
            ShortMusicResource.BOMB_EXPLOSION -> R.raw.bomb_explosion
        }
    }

    private val mediaPlayers = mutableMapOf<UInt, MediaPlayer>()
    private var mediaIdx = 0u
    override fun playLong(music: LongMusicResource): UInt {
        ++mediaIdx
        val player = createMediaPlayer(mapLongMusicResource(music))
        mediaPlayers[mediaIdx] = player
        player.start()
        player.isLooping = true
        return mediaIdx
    }

    override fun pauseLong(id: UInt) {
        mediaPlayers[id]!!.pause()
    }

    override fun resumeLong(id: UInt) {
        mediaPlayers[id]!!.start()
    }

    override fun stopLong(id: UInt) {
        mediaPlayers[id]!!.stop()
        mediaPlayers[id]!!.release()
        mediaPlayers.remove(id)
    }

    override fun playShort(music: ShortMusicResource): Int {
        return soundPool.play(shortMusicMap[music]!!, 1f, 1f, 0, 0, 1f)
    }

    override fun pauseShort(id: Int) {
        soundPool.pause(id)
    }

    override fun resumeShort(id: Int) {
        soundPool.resume(id)
    }

    override fun stopShort(id: Int) {
        soundPool.stop(id)
    }
}