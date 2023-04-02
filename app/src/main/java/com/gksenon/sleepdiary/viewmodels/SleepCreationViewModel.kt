package com.gksenon.sleepdiary.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gksenon.sleepdiary.data.Sleep
import com.gksenon.sleepdiary.data.SleepDiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SleepCreationViewModel @Inject constructor(private val sleepDiaryRepository: SleepDiaryRepository) :
    ViewModel() {

        fun saveSleep(startDate: String, startTime: String, endDate: String, endTime: String) {
            val dateTimeFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
            val startDateTime = dateTimeFormat.parse("$startDate $startTime") ?: Date()
            val endDateTime = dateTimeFormat.parse("$endDate $endTime") ?: Date()

            viewModelScope.launch {
                val sleep = Sleep(UUID.randomUUID(), startDateTime, endDateTime)
                sleepDiaryRepository.saveSleep(sleep)
            }
        }
}