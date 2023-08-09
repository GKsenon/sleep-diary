package com.gksenon.sleepdiary.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import java.util.UUID

@Dao
internal interface SleepDao {

    @Query("SELECT * FROM sleep ORDER BY start DESC")
    fun getSleepDiary(): Flow<List<SleepEntity>>

    @Query("SELECT * FROM sleep WHERE sleep.id = :id")
    fun getSleep(id: UUID): Flow<SleepEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveSleep(sleep: SleepEntity)

    @Query("DELETE FROM sleep WHERE sleep.id = :id")
    fun deleteSleep(id: UUID)
}