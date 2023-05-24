package com.gksenon.sleepdiary.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
interface SleepDao {

    @Query("SELECT * FROM sleep ORDER BY start DESC")
    fun getSleepDiary(): Flow<List<Sleep>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSleep(sleep: Sleep)

    @Query("SELECT * FROM sleep WHERE sleep.id = :sleepId")
    fun getSleep(sleepId: UUID): Flow<Sleep>
}