package com.gksenon.sleepdiary.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID

class Tracker(private val diary: Diary, private val gateway: Gateway) {

    suspend fun start() = gateway.saveStartTime(Date())

    fun observe(): Flow<Event> = gateway.getStartTime().map { start ->
        if (start != null)
            Event.Started(start)
        else
            Event.Stopped
    }

    suspend fun updateStartTime(start: Date) = gateway.saveStartTime(start)

    suspend fun stop() {
        val start = gateway.getStartTime().first()
        if (start != null) {
            diary.saveSleep(Sleep(UUID.randomUUID(), start, Date()))
        }
        gateway.clearStartTime()
    }

    sealed class Event {

        data class Started(val start: Date) : Event()

        object Stopped : Event()
    }

    interface Gateway {
        fun getStartTime(): Flow<Date?>

        suspend fun saveStartTime(start: Date)

        suspend fun clearStartTime()
    }
}