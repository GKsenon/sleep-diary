package com.gksenon.sleepdiary.data

import androidx.room.TypeConverter
import java.util.*

class TimeConverter {

    @TypeConverter
    fun fromTimestamp(timestamp: Long): Date = Date(timestamp)

    @TypeConverter
    fun toTimestamp(date: Date): Long = date.time
}