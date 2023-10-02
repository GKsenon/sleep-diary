package com.gksenon.sleepdiary.data

import com.gksenon.sleepdiary.domain.Diary
import com.gksenon.sleepdiary.domain.Sleep
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import java.util.UUID

private const val CREATE_SLEEP_PATH = "/create"
private const val UPDATE_SLEEP_PATH = "/update"
private const val DELETE_SLEEP_PATH = "/delete"

private const val SLEEP_ID_KEY = "ID"
private const val SLEEP_START_KEY = "START"
private const val SLEEP_END_KEY = "END"

internal class SynchronizedRoomDiary(
    private val sleepDao: SleepDao,
    private val dataClient: DataClient,
    private val coroutineScope: CoroutineScope
) : Diary {

    init {
        dataClient.addListener { dataEvents ->
            dataEvents.forEach { dataEvent ->
                when (dataEvent.dataItem.uri.path) {
                    CREATE_SLEEP_PATH -> onCreateSleepDataEvent(dataEvent)
                    UPDATE_SLEEP_PATH -> onUpdateSleepDataEvent(dataEvent)
                    DELETE_SLEEP_PATH -> onDeleteSleepDataEvent(dataEvent)
                }
            }
        }
    }

    override fun getSleepDiary(): Flow<List<Sleep>> = sleepDao.getSleepDiary().map { diary ->
        diary.map { Sleep(it.id, it.start, it.end) }
    }

    override fun getSleep(id: UUID): Flow<Sleep?> = sleepDao.getSleep(id).map { sleepEntity ->
        if (sleepEntity != null) Sleep(sleepEntity.id, sleepEntity.start, sleepEntity.end) else null
    }

    override suspend fun saveSleep(sleep: Sleep) = when (saveSleepEntry(sleep)) {
        SleepDao.Result.SUCCESS -> {
            sendCreateSleepDataEvent(sleep)
            Diary.Result.SUCCESS
        }

        SleepDao.Result.CONFLICT -> Diary.Result.CONFLICT
    }

    override suspend fun updateSleep(sleep: Sleep) = when (saveSleepEntry(sleep)) {
        SleepDao.Result.SUCCESS -> {
            sendUpdateSleepDataEvent(sleep)
            Diary.Result.SUCCESS
        }

        SleepDao.Result.CONFLICT -> Diary.Result.CONFLICT
    }

    override suspend fun deleteSleep(id: UUID) {
        withContext(Dispatchers.IO) {
            sleepDao.deleteSleep(id)
        }
        sendDeleteSleepDataEvent(id)
    }

    private fun onCreateSleepDataEvent(dataEvent: DataEvent) {
        val dataMap = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
        val id = UUID.fromString(dataMap.getString(SLEEP_ID_KEY))
        val start = Date(dataMap.getLong(SLEEP_START_KEY))
        val end = Date(dataMap.getLong(SLEEP_END_KEY))
        val sleep = Sleep(id, start, end)
        coroutineScope.launch { saveSleepEntry(sleep) }
    }

    private fun onUpdateSleepDataEvent(dataEvent: DataEvent) {
        val dataMap = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
        val id = UUID.fromString(dataMap.getString(SLEEP_ID_KEY))
        val start = Date(dataMap.getLong(SLEEP_START_KEY))
        val end = Date(dataMap.getLong(SLEEP_END_KEY))
        val sleep = Sleep(id, start, end)
        coroutineScope.launch { saveSleepEntry(sleep) }
    }

    private fun onDeleteSleepDataEvent(dataEvent: DataEvent) {
        val dataMap = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
        val id = UUID.fromString(dataMap.getString(SLEEP_ID_KEY))
        coroutineScope.launch { withContext(Dispatchers.IO) { sleepDao.deleteSleep(id) } }
    }

    private suspend fun sendCreateSleepDataEvent(sleep: Sleep) {
        val sleepDataItem = PutDataMapRequest.create(CREATE_SLEEP_PATH).apply {
            dataMap.putString(SLEEP_ID_KEY, sleep.id.toString())
            dataMap.putLong(SLEEP_START_KEY, sleep.start.time)
            dataMap.putLong(SLEEP_END_KEY, sleep.end.time)
        }
            .asPutDataRequest()
            .setUrgent()

        dataClient.putDataItem(sleepDataItem).await()
    }

    private suspend fun sendUpdateSleepDataEvent(sleep: Sleep) {
        val updateSleepDataItem = PutDataMapRequest.create(UPDATE_SLEEP_PATH).apply {
            dataMap.putString(SLEEP_ID_KEY, sleep.id.toString())
            dataMap.putLong(SLEEP_START_KEY, sleep.start.time)
            dataMap.putLong(SLEEP_END_KEY, sleep.end.time)
        }
            .asPutDataRequest()
            .setUrgent()

        dataClient.putDataItem(updateSleepDataItem).await()
    }

    private suspend fun sendDeleteSleepDataEvent(id: UUID) {
        val deleteSleepDataItem = PutDataMapRequest.create(DELETE_SLEEP_PATH).apply {
            dataMap.putString(SLEEP_ID_KEY, id.toString())
        }
            .asPutDataRequest()
            .setUrgent()

        dataClient.putDataItem(deleteSleepDataItem).await()
    }

    private suspend fun saveSleepEntry(sleep: Sleep): SleepDao.Result = withContext(Dispatchers.IO) {
        sleepDao.saveSleep(SleepEntity(sleep.id, sleep.start, sleep.end))
    }
}