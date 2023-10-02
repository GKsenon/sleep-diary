package com.gksenon.sleepdiary.domain

import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface Diary {

    fun getSleepDiary(): Flow<List<Sleep>>

    fun getSleep(id: UUID): Flow<Sleep?>

    suspend fun saveSleep(sleep: Sleep): Result

    suspend fun updateSleep(sleep: Sleep): Result

    suspend fun deleteSleep(id: UUID)

    enum class Result {
        SUCCESS, CONFLICT
    }
}