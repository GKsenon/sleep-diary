package com.gksenon.sleepdiary.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.UUID

@Dao
internal abstract class SleepDao {

    @Query("SELECT * FROM sleep ORDER BY start DESC")
    abstract fun getSleepDiary(): Flow<List<SleepEntity>>

    @Query("SELECT * FROM sleep WHERE sleep.id = :id")
    abstract fun getSleep(id: UUID): Flow<SleepEntity?>

    @Transaction
    open fun saveSleep(sleep: SleepEntity): Result {
        val conflicts = getConflicts(sleep.start, sleep.end)
        return if(conflicts == 0) {
            insertSleep(sleep)
            Result.SUCCESS
        } else {
            Result.CONFLICT
        }
    }

    @Query("DELETE FROM sleep WHERE sleep.id = :id")
    abstract fun deleteSleep(id: UUID)

    @Query("SELECT COUNT(*) FROM sleep WHERE sleep.start >= :start AND sleep.start <= :end " +
            "OR sleep.`end` >= :start AND sleep.`end` <= :end " +
            "OR sleep.start <= :start AND sleep.`end` >= :end")
    abstract fun getConflicts(start: Date, end: Date): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertSleep(sleep: SleepEntity)

    enum class Result {
        SUCCESS, CONFLICT
    }
}