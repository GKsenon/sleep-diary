package com.gksenon.sleepdiary.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.gksenon.sleepdiary.domain.Diary
import com.gksenon.sleepdiary.domain.Sleep
import com.gksenon.sleepdiary.domain.Tracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import java.util.UUID

private val Context.dataStore by preferencesDataStore(name = "sleep_tracker")

class DataStoreTracker(private val diary: Diary, private val context: Context) : Tracker {

    private val startTimestampKey = longPreferencesKey(name = "start_timestamp")

    override suspend fun start() {
        context.dataStore.edit { trackerData ->
            val currentDate = Date()
            trackerData[startTimestampKey] = currentDate.time
        }
    }

    override fun observe(): Flow<Tracker.Event> =
        context.dataStore.data.map { trackerData ->
            val startTimestamp = trackerData[startTimestampKey]
            if (startTimestamp != null && startTimestamp != 0L)
                Tracker.Event.Started(Date(startTimestamp))
            else
                Tracker.Event.Stopped
        }

    override suspend fun updateStartTime(start: Date) {
        context.dataStore.edit { trackerData ->
            trackerData[startTimestampKey] = start.time
        }
    }

    override suspend fun stop() {
        context.dataStore.edit { trackerData ->
            val startTimestamp = trackerData[startTimestampKey]
            if (startTimestamp != null && startTimestamp != 0L) {
                val uuid = UUID.randomUUID()
                val start = Date(startTimestamp)
                val end = Date()
                diary.saveSleep(Sleep(uuid, start, end))
            }
            trackerData[startTimestampKey] = 0
        }
    }
}