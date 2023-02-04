package com.gksenon.sleepdiary.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Sleep::class], version = 1)
@TypeConverters(TimeConverter::class)
abstract class SleepDiaryDatabase : RoomDatabase() {
    abstract fun sleepDao(): SleepDao
}