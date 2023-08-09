package com.gksenon.sleepdiary.data

import com.gksenon.sleepdiary.domain.Diary
import com.gksenon.sleepdiary.domain.Sleep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

internal class RoomDiary(private val sleepDao: SleepDao) : Diary {

    override fun getSleepDiary(): Flow<List<Sleep>> = sleepDao.getSleepDiary().map { diary ->
        diary.map { Sleep(it.id, it.start, it.end) }
    }

    override fun getSleep(id: UUID): Flow<Sleep?> = sleepDao.getSleep(id).map { sleepEntity ->
        if (sleepEntity != null) Sleep(sleepEntity.id, sleepEntity.start, sleepEntity.end) else null
    }

    override suspend fun saveSleep(sleep: Sleep) = withContext(Dispatchers.IO) {
        sleepDao.saveSleep(SleepEntity(sleep.id, sleep.start, sleep.end))
    }

    override suspend fun updateSleep(sleep: Sleep) = withContext(Dispatchers.IO) {
        sleepDao.saveSleep(SleepEntity(sleep.id, sleep.start, sleep.end))
    }

    override suspend fun deleteSleep(id: UUID) = withContext(Dispatchers.IO) {
        sleepDao.deleteSleep(id)
    }
}