package com.gksenon.sleepdiary.data

import com.gksenon.sleepdiary.domain.Diary
import com.gksenon.sleepdiary.domain.Sleep
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

private const val CREATE_SLEEP_PATH = "/create"
private const val UPDATE_SLEEP_PATH = "/update"
private const val DELETE_SLEEP_PATH = "/delete"

private const val SLEEP_ID_KEY = "ID"
private const val SLEEP_START_KEY = "START"
private const val SLEEP_END_KEY = "END"

class SynchronizedDiary(
    private val diary: Diary,
    private val dataClient: DataClient,
    private val coroutineScope: CoroutineScope
): Diary {

    init {
        dataClient.addListener { dataEvents ->
            dataEvents.forEach { dataEvent ->
                when(dataEvent.dataItem.uri.path) {
                    CREATE_SLEEP_PATH -> onCreateSleepDataEvent(dataEvent)
                    UPDATE_SLEEP_PATH -> onUpdateSleepDataEvent(dataEvent)
                    DELETE_SLEEP_PATH -> onDeleteSleepDataEvent(dataEvent)
                }
            }
        }
    }

    override fun getSleepDiary(): Flow<List<Sleep>> = diary.getSleepDiary()

    override fun getSleep(id: UUID): Flow<Sleep?> = diary.getSleep(id)

    override suspend fun saveSleep(sleep: Sleep) {
        diary.saveSleep(sleep)

        val sleepDataItem = PutDataMapRequest.create(CREATE_SLEEP_PATH).apply {
            dataMap.putString(SLEEP_ID_KEY, sleep.id.toString())
            dataMap.putLong(SLEEP_START_KEY, sleep.start.time)
            dataMap.putLong(SLEEP_END_KEY, sleep.end.time)
        }
            .asPutDataRequest()
            .setUrgent()

        dataClient.putDataItem(sleepDataItem).await()
    }

    override suspend fun updateSleep(sleep: Sleep) {
        diary.updateSleep(sleep)

        val updateSleepDataItem = PutDataMapRequest.create(UPDATE_SLEEP_PATH).apply {
            dataMap.putString(SLEEP_ID_KEY, sleep.id.toString())
            dataMap.putLong(SLEEP_START_KEY, sleep.start.time)
            dataMap.putLong(SLEEP_END_KEY, sleep.end.time)
        }
            .asPutDataRequest()
            .setUrgent()

        dataClient.putDataItem(updateSleepDataItem).await()
    }

    override suspend fun deleteSleep(id: UUID) {
        diary.deleteSleep(id)

        val deleteSleepDataItem = PutDataMapRequest.create(DELETE_SLEEP_PATH).apply {
            dataMap.putString(SLEEP_ID_KEY, id.toString())
        }
            .asPutDataRequest()
            .setUrgent()

        dataClient.putDataItem(deleteSleepDataItem).await()
    }

    private fun onCreateSleepDataEvent(dataEvent: DataEvent) {
        val dataMap = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
        val id = UUID.fromString(dataMap.getString(SLEEP_ID_KEY))
        val start = Date(dataMap.getLong(SLEEP_START_KEY))
        val end = Date(dataMap.getLong(SLEEP_END_KEY))
        val sleep = Sleep(id, start, end)
        coroutineScope.launch { diary.saveSleep(sleep) }
    }

    private fun onUpdateSleepDataEvent(dataEvent: DataEvent) {
        val dataMap = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
        val id = UUID.fromString(dataMap.getString(SLEEP_ID_KEY))
        val start = Date(dataMap.getLong(SLEEP_START_KEY))
        val end = Date(dataMap.getLong(SLEEP_END_KEY))
        val sleep = Sleep(id, start, end)
        coroutineScope.launch { diary.updateSleep(sleep) }
    }

    private fun onDeleteSleepDataEvent(dataEvent: DataEvent) {
        val dataMap = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
        val id = UUID.fromString(dataMap.getString(SLEEP_ID_KEY))
        coroutineScope.launch { diary.deleteSleep(id) }
    }
}