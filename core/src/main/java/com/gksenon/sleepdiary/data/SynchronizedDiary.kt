package com.gksenon.sleepdiary.data

import com.gksenon.sleepdiary.domain.Diary
import com.gksenon.sleepdiary.domain.Sleep
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

private const val SLEEP_ID_KEY = "ID"
private const val SLEEP_START_KEY = "TYPE"
private const val SLEEP_END_KEY = "TIMESTAMP"

class SynchronizedDiary(
    private val diary: Diary,
    private val dataClient: DataClient,
    private val coroutineScope: CoroutineScope
): Diary {

    init {
        dataClient.addListener { dataEvents ->
            dataEvents.forEach { dataEvent ->
                val dataMap = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
                val id = UUID.fromString(dataMap.getString(SLEEP_ID_KEY))
                val start = Date(dataMap.getLong(SLEEP_START_KEY))
                val end = Date(dataMap.getLong(SLEEP_END_KEY))
                val sleep = Sleep(id, start, end)
                coroutineScope.launch { diary.saveSleep(sleep) }
            }
        }
    }

    override fun getSleepDiary(): Flow<List<Sleep>> = diary.getSleepDiary()

    override fun getSleep(id: UUID): Flow<Sleep?> = diary.getSleep(id)

    override suspend fun saveSleep(sleep: Sleep) {
        diary.saveSleep(sleep)

        val sleepDataItem = PutDataMapRequest.create("/sleep").apply {
            dataMap.putString(SLEEP_ID_KEY, sleep.id.toString())
            dataMap.putLong(SLEEP_START_KEY, sleep.start.time)
            dataMap.putLong(SLEEP_END_KEY, sleep.end.time)
        }
            .asPutDataRequest()
            .setUrgent()

        dataClient.putDataItem(sleepDataItem).await()
    }

    override suspend fun updateSleep(sleep: Sleep) = diary.updateSleep(sleep)

    override suspend fun deleteSleep(id: UUID) = diary.deleteSleep(id)
}