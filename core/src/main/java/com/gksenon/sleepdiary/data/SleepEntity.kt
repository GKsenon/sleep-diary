package com.gksenon.sleepdiary.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "sleep")
internal data class SleepEntity(
    @PrimaryKey val id: UUID,
    val start: Date,
    val end: Date
)