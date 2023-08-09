package com.gksenon.sleepdiary.domain

import kotlinx.coroutines.flow.Flow
import java.util.Date

interface Tracker {

    suspend fun start()

    fun observe(): Flow<Event>

    suspend fun updateStartTime(start: Date)

    suspend fun stop()

    sealed class Event {

        data class Started(val start: Date) : Event()

        object Stopped : Event()
    }
}