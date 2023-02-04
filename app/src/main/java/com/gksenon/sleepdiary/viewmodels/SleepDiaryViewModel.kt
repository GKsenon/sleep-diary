package com.gksenon.sleepdiary.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.gksenon.sleepdiary.data.Sleep
import com.gksenon.sleepdiary.data.SleepDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SleepDiaryViewModel @Inject constructor(private val sleepDao: SleepDao) :
    ViewModel() {

    val sleepDiary: LiveData<List<Sleep>> by lazy {
        sleepDao.getSleepDiary()
    }
}