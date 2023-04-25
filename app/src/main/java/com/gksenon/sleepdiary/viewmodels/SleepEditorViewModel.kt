package com.gksenon.sleepdiary.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gksenon.sleepdiary.data.Sleep
import com.gksenon.sleepdiary.data.SleepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SleepEditorViewModel @Inject constructor(
    private val sleepRepository: SleepRepository
) : ViewModel() {

    private val dateFormatter = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        .apply { isLenient = false }
    private val timeFormatter = SimpleDateFormat("HHmm", Locale.getDefault())
        .apply { isLenient = false }

    private var _sleepEditorState = MutableStateFlow(
        SleepEditorState(
            startDate = dateFormatter.format(Date()),
            startTime = timeFormatter.format(Date()),
            endDate = dateFormatter.format(Date()),
            endTime = timeFormatter.format(Date())
        )
    )
    val sleepEditorState = _sleepEditorState.asStateFlow()

    fun onStartDateChanged(value: String) {
        _sleepEditorState.update { currentState ->
            currentState.copy(
                startDate = value.filter { it.isDigit() }.take(8),
                showStartDateError = false
            )
        }
    }

    fun onStartTimeChanged(value: String) {
        _sleepEditorState.update { currentState ->
            currentState.copy(
                startTime = value.filter { it.isDigit() }.take(4),
                showStartTimeError = false
            )
        }
    }

    fun onEndDateChanged(value: String) {
        _sleepEditorState.update { currentState ->
            currentState.copy(
                endDate = value.filter { it.isDigit() }.take(8),
                showEndDateError = false
            )
        }
    }

    fun onEndTimeChanged(value: String) {
        _sleepEditorState.update { currentState ->
            currentState.copy(
                endTime = value.filter { it.isDigit() }.take(4),
                showEndTimeError = false
            )
        }
    }

    fun saveSleep(onSaveSleep: () -> Unit) {
        val currentState = _sleepEditorState.value

        val startDate = parseDate(currentState.startDate)
        val startTime = parseTime(currentState.startTime)
        val endDate = parseDate(currentState.endDate)
        val endTime = parseTime(currentState.endTime)

        if (startDate != null && startTime != null && endDate != null && endTime != null) {
            viewModelScope.launch {
                val sleep = Sleep(
                    id = UUID.randomUUID(),
                    start = merge(startDate, startTime),
                    end = merge(endDate, endTime)
                )
                sleepRepository.saveSleep(sleep)
            }
            onSaveSleep()
        } else {
            _sleepEditorState.update {
                it.copy(
                    showStartDateError = startDate == null,
                    showStartTimeError = startTime == null,
                    showEndDateError = endDate == null,
                    showEndTimeError = endTime == null
                )
            }
        }
    }

    private fun parseDate(date: String): Date? = try {
        dateFormatter.parse(date)
    } catch (e: ParseException) {
        null
    }

    private fun parseTime(time: String): Date? = try {
        timeFormatter.parse(time)
    } catch (e: ParseException) {
        null
    }

    private fun merge(date: Date, time: Date): Date {
        val dateCalendar = Calendar.getInstance().also { it.time = date }
        val timeCalendar = Calendar.getInstance().also { it.time = time }
        dateCalendar.add(Calendar.HOUR, timeCalendar.get(Calendar.HOUR_OF_DAY))
        dateCalendar.add(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
        return dateCalendar.time
    }
}

data class SleepEditorState(
    val startDate: String,
    val showStartDateError: Boolean = false,
    val startTime: String,
    val showStartTimeError: Boolean = false,
    val endDate: String,
    val showEndDateError: Boolean = false,
    val endTime: String,
    val showEndTimeError: Boolean = false
)
