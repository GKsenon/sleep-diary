package com.gksenon.sleepdiary.domain

import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface Diary {

    fun getSleepDiary(): Flow<List<Sleep>>

    fun getSleep(id: UUID): Flow<Sleep?>

    suspend fun saveSleep(sleep: Sleep)

    suspend fun updateSleep(sleep: Sleep)

    suspend fun deleteSleep(id: UUID)
}