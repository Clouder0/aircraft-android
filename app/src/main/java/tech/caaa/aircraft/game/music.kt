package tech.caaa.aircraft.game

enum class LongMusicResource {
    BGM,
    BGM_BOSS,
}

enum class ShortMusicResource {
    BULLET_HIT,
    BOMB_EXPLOSION,
    GET_SUPPLY,
    GAME_OVER
}

interface MusicHelper {
    // return music id
    fun playShort(music: ShortMusicResource): Int
    fun playLong(music: LongMusicResource): UInt
    fun pauseShort(id: Int)
    fun pauseLong(id: UInt)
    fun stopShort(id: Int)
    fun stopLong(id: UInt)
    fun resumeShort(id: Int)
    fun resumeLong(id: UInt)
}