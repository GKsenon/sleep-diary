package com.gksenon.sleepdiary.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gksenon.sleepdiary.data.Sleep
import com.gksenon.sleepdiary.data.SleepDao
import com.gksenon.sleepdiary.data.SleepDiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SleepCreationViewModel @Inject constructor(private val sleepDiaryRepository: SleepDiaryRepository) :
    ViewModel() {

    private var startDate: MutableLiveData<Date> = MutableLiveData(Date())
    private var endDate: MutableLiveData<Date> = MutableLiveData(Date())
    private val saveStatus: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getStartDate(): LiveData<Date> = startDate

    fun getEndDate(): LiveData<Date> = endDate

    fun getSaveStatus(): LiveData<Boolean> = saveStatus

    fun updateStartDate(timestamp: Long) {
        startDate.value?.let { startDate.postValue(updateDate(it, timestamp)) }
    }

    fun updateStartTime(hour: Int, minute: Int) {
        startDate.value?.let { startDate.postValue(updateTime(it, hour, minute)) }
    }

    fun updateEndDate(timestamp: Long) {
        endDate.value?.let { endDate.postValue(updateDate(it, timestamp)) }
    }

    fun updateEndTime(hour: Int, minute: Int) {
        endDate.value?.let { endDate.postValue(updateTime(it, hour, minute)) }
    }

    fun saveSleep() {
        viewModelScope.launch {
            val sleep = Sleep(UUID.randomUUID(), startDate.value ?: Date(), endDate.value ?: Date())
            sleepDiaryRepository.saveSleep(sleep)
            saveStatus.postValue(true)
        }
    }

    private fun updateDate(old: Date, timestamp: Long): Date {
        val oldCalendar = Calendar.getInstance().apply { time = old }
        val newCalendar = Calendar.getInstance().apply { time = Date(timestamp) }
        newCalendar.set(Calendar.HOUR_OF_DAY, oldCalendar.get(Calendar.HOUR_OF_DAY))
        newCalendar.set(Calendar.MINUTE, oldCalendar.get(Calendar.MINUTE))
        return newCalendar.time
    }

    private fun updateTime(old: Date, hour: Int, minute: Int): Date {
        val calendar = Calendar.getInstance().apply { time = old }
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        return calendar.time
    }
}