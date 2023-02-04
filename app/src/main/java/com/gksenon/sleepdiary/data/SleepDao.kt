package com.gksenon.sleepdiary.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SleepDao {

    @Query("SELECT * FROM sleep")
    fun getSleepDiary(): LiveData<List<Sleep>>

    @Insert
    fun saveSleep(sleep: Sleep)
}