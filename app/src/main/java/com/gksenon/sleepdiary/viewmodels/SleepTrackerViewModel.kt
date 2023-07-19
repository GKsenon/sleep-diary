package com.gksenon.sleepdiary.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gksenon.sleepdiary.data.SleepRepository
import com.gksenon.sleepdiary.notifications.SleepTrackerNotificationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SleepTrackerViewModel @Inject constructor(
    private val sleepRepository: SleepRepository,
    private val notificationManager: SleepTrackerNotificationManager
) :
    ViewModel() {

    private val dateFormatter = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        .apply { isLenient = false }
    private val timeFormatter = SimpleDateFormat("HHmm", Locale.getDefault())
        .apply { isLenient = false }

    private var _state: MutableStateFlow<TrackerState> = MutableStateFlow(TrackerState.Stopped)
    val state = _state.asStateFlow()

    init {
        sleepRepository.observeTracker()
            .onEach { trackerEvent ->
                when (trackerEvent) {
                    is SleepRepository.TrackerEvent.Started -> onTrackerStarted(trackerEvent.start)
                    is SleepRepository.TrackerEvent.Stopped -> onTrackerStopped()
                }
            }
            .launchIn(viewModelScope)
    }

    fun startTracking() {
        viewModelScope.launch { sleepRepository.startTracking() }
    }

    fun stopTracking() {
        viewModelScope.launch { sleepRepository.stopTracking() }
    }

    fun editStartDate() {
        _state.update { currentState ->
            if (currentState is TrackerState.Tracking) {
                currentState.copy(isStartDateEditingEnabled = true)
            } else {
                currentState
            }
        }
    }

    fun onStartDateChanged(start: String) {
        _state.update { currentState ->
            if (currentState is TrackerState.Tracking) {
                currentState.copy(
                    formattedStartDate = start.filter { it.isDigit() }.take(8),
                    showStartDateInvalidFormatError = false,
                    showStartDateInFutureError = false
                )
            } else {
                currentState
            }
        }
    }

    fun saveStartDate() {
        _state.update { currentState ->
            if (currentState is TrackerState.Tracking) {
                val newStartDate = parseDate(currentState.formattedStartDate)
                if (newStartDate != null) {
                    val newStartCalendar = Calendar.getInstance().apply { time = newStartDate }
                    val oldStartCalendar =
                        Calendar.getInstance().apply { time = currentState.startDate }

                    newStartCalendar.set(
                        Calendar.HOUR_OF_DAY,
                        oldStartCalendar.get(Calendar.HOUR_OF_DAY)
                    )
                    newStartCalendar.set(Calendar.MINUTE, oldStartCalendar.get(Calendar.MINUTE))

                    if (newStartCalendar.time.after(Date())) {
                        currentState.copy(showStartDateInFutureError = true)
                    } else {
                        viewModelScope.launch {
                            sleepRepository.updateTrackerStart(newStartCalendar.time)
                        }
                        currentState.copy(isStartDateEditingEnabled = false)
                    }

                } else {
                    currentState.copy(showStartDateInvalidFormatError = true)
                }
            } else {
                currentState
            }
        }
    }

    fun editStartTime() {
        _state.update { currentState ->
            if (currentState is TrackerState.Tracking) {
                currentState.copy(isStartTimeEditingEnabled = true)
            } else {
                currentState
            }
        }
    }

    fun onStartTimeChanged(startTime: String) {
        _state.update { currentState ->
            if (currentState is TrackerState.Tracking) {
                currentState.copy(
                    formattedStartTime = startTime.filter { it.isDigit() }.take(4),
                    showStartTimeInvalidFormatError = false,
                    showStartTimeInFutureError = false
                )
            } else {
                currentState
            }
        }
    }

    fun saveStartTime() {
        _state.update { currentState ->
            if (currentState is TrackerState.Tracking) {
                val newStartTime = parseTime(currentState.formattedStartTime)
                if (newStartTime != null) {
                    val newStartTimeCalendar = Calendar.getInstance().apply { time = newStartTime }
                    val oldStartDateCalendar =
                        Calendar.getInstance().apply { time = currentState.startDate }
                    newStartTimeCalendar.set(Calendar.YEAR, oldStartDateCalendar.get(Calendar.YEAR))
                    newStartTimeCalendar.set(
                        Calendar.MONTH,
                        oldStartDateCalendar.get(Calendar.MONTH)
                    )
                    newStartTimeCalendar.set(
                        Calendar.DAY_OF_MONTH,
                        oldStartDateCalendar.get(Calendar.DAY_OF_MONTH)
                    )

                    if (newStartTimeCalendar.time.after(Date())) {
                        currentState.copy(showStartTimeInFutureError = true)
                    } else {
                        viewModelScope.launch {
                            sleepRepository.updateTrackerStart(newStartTimeCalendar.time)
                        }
                        currentState.copy(isStartTimeEditingEnabled = false)
                    }
                } else {
                    currentState.copy(showStartTimeInvalidFormatError = true)
                }
            } else {
                currentState
            }
        }
    }

    private fun onTrackerStarted(start: Date) {
        _state.update {
            TrackerState.Tracking(
                startDate = start,
                formattedStartDate = dateFormatter.format(start),
                formattedStartTime = timeFormatter.format(start)
            )
        }

        notificationManager.showNotification(start)
    }

    private fun onTrackerStopped() {
        _state.update { TrackerState.Stopped }
        notificationManager.hideNotification()
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
}

sealed class TrackerState {

    data class Tracking(
        val startDate: Date,
        val formattedStartDate: String,
        val formattedStartTime: String,
        val isStartDateEditingEnabled: Boolean = false,
        val isStartTimeEditingEnabled: Boolean = false,
        val showStartDateInvalidFormatError: Boolean = false,
        val showStartDateInFutureError: Boolean = false,
        val showStartTimeInvalidFormatError: Boolean = false,
        val showStartTimeInFutureError: Boolean = false
    ) : TrackerState()

    object Stopped : TrackerState()
}