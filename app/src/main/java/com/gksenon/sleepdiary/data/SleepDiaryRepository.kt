package com.gksenon.sleepdiary.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SleepDiaryRepository(private val sleepDao: SleepDao) {

    suspend fun saveSleep(sleep: Sleep) = withContext(Dispatchers.IO) { sleepDao.saveSleep(sleep) }
}