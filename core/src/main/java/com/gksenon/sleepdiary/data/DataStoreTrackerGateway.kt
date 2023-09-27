package com.gksenon.sleepdiary.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gksenon.sleepdiary.domain.Tracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

private val Context.dataStore by preferencesDataStore(name = "sleep_tracker")

internal class DataStoreTrackerGateway(private val context: Context) : Tracker.Gateway {

    private val startTimestampKey = longPreferencesKey(name = "start_timestamp")

    override fun getStartTime(): Flow<Date?> = context.dataStore.data.map { trackerData ->
        val startTimestamp = trackerData[startTimestampKey]
        if (startTimestamp != null && startTimestamp != 0L)
            Date(startTimestamp)
        else
            null
    }

    override suspend fun saveStartTime(start: Date) {
        context.dataStore.edit { trackerData ->
            trackerData[startTimestampKey] = start.time
        }
    }

    override suspend fun clearStartTime() {
        context.dataStore.edit { trackerData ->
            trackerData[startTimestampKey] = 0
        }
    }
}