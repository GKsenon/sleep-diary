package com.gksenon.sleepdiary.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {

    @Query("SELECT * FROM sleep ORDER BY start DESC")
    fun getSleepDiary(): Flow<List<Sleep>>

    @Insert
    fun saveSleep(sleep: Sleep)
}