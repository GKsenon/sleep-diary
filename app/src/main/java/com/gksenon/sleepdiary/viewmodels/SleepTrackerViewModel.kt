package com.gksenon.sleepdiary.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gksenon.sleepdiary.data.SleepRepository
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
import java.util.Date
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SleepTrackerViewModel @Inject constructor(private val sleepRepository: SleepRepository) :
    ViewModel() {

    private var _state: MutableStateFlow<TrackerState> = MutableStateFlow(TrackerState.Stopped)
    val state = _state.asStateFlow()

    private var tickerFlow: Flow<Unit> = flow {
        delay(1.seconds)
        while (true) {
            emit(Unit)
            delay(1.seconds)
        }
    }
    private var tickerJob: Job? = null

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

    private fun onTrackerStarted(start: Date) {
        _state.update {
            val currentTimestamp = Date().time
            val trackerDuration = (currentTimestamp - start.time).milliseconds
            TrackerState.Tracking(trackerDuration)
        }
        tickerJob = tickerFlow.onEach {
            _state.update { currentState ->
                if (currentState is TrackerState.Tracking) {
                    val duration = currentState.duration + 1.seconds
                    TrackerState.Tracking(duration)
                } else {
                    currentState
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun onTrackerStopped() {
        tickerJob?.cancel()
        _state.update { TrackerState.Stopped }
    }
}

sealed class TrackerState {

    data class Tracking(val duration: Duration) : TrackerState()

    object Stopped : TrackerState()
}