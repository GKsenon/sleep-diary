package com.gksenon.sleepdiary.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "sleep")
data class Sleep(
    @PrimaryKey val id: UUID,
    val start: Date,
    val end: Date
)