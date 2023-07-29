package com.gksenon.sleepdiary.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

private val Context.dataStore by preferencesDataStore(name = "sleep_tracker")

class SleepRepository(private val context: Context, private val sleepDao: SleepDao) {

    private val startTimestampKey = longPreferencesKey(name = "start_timestamp")

    fun getSleepDiary(): Flow<List<Sleep>> = sleepDao.getSleepDiary()

    fun getSleep(sleepId: UUID): Flow<Sleep?> = sleepDao.getSleep(sleepId)

    suspend fun saveSleep(sleep: Sleep) = withContext(Dispatchers.IO) { sleepDao.saveSleep(sleep) }

    suspend fun deleteSleep(sleepId: UUID) = withContext(Dispatchers.IO) { sleepDao.deleteSleep(sleepId) }

    suspend fun startTracking() {
        context.dataStore.edit { trackerData ->
            val currentDate = Date()
            trackerData[startTimestampKey] = currentDate.time
        }
    }

    fun observeTracker(): Flow<TrackerEvent> =
        context.dataStore.data.map { trackerData ->
            val startTimestamp = trackerData[startTimestampKey]
            if (startTimestamp != null && startTimestamp != 0L)
                TrackerEvent.Started(Date(startTimestamp))
            else
                TrackerEvent.Stopped
        }

    suspend fun stopTracking() {
        context.dataStore.edit { trackerData ->
            val startTimestamp = trackerData[startTimestampKey]
            if (startTimestamp != null && startTimestamp != 0L) {
                val uuid = UUID.randomUUID()
                val start = Date(startTimestamp)
                val end = Date()
                saveSleep(Sleep(uuid, start, end))
            }
            trackerData[startTimestampKey] = 0
        }
    }

    suspend fun updateTrackerStart(start: Date) {
        context.dataStore.edit { trackerData ->
            trackerData[startTimestampKey] = start.time
        }
    }

    sealed class TrackerEvent {
        data class Started(val start: Date) : TrackerEvent()

        object Stopped : TrackerEvent()
    }
}