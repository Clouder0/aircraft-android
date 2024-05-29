package tech.caaa.aircraft.game

import tech.caaa.aircraft.aircrafts.HeroAircraft

sealed class Event(val frameCnt: UInt, val callback: () -> Unit = {}) {
    class HeroEnhanceBulletExpireEvent(frameCnt: UInt, callback: () -> Unit) : Event(frameCnt, callback)
}


class EventManager {
    val events = mutableMapOf<UInt, ArrayList<Event>>()

    fun registerEvent(e: Event) {
        val queue = events.getOrPut(e.frameCnt){
            ArrayList()
        }
        queue.add(e)
    }

    fun cancelEvent(e: Event): Boolean {
        val queue = events[e.frameCnt] ?: return false
        return queue.remove(e)
    }

    fun eventsAtFrame(cnt: UInt): List<Event> {
        return events.getOrDefault(cnt, emptyList())
    }

}