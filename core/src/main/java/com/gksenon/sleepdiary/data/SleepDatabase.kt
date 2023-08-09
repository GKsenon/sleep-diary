package com.gksenon.sleepdiary.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [SleepEntity::class], version = 1)
@TypeConverters(TimeConverter::class)
internal abstract class SleepDatabase: RoomDatabase() {

    abstract fun sleepDao(): SleepDao
}