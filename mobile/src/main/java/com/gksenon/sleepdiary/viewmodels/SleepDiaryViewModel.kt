package com.gksenon.sleepdiary.viewmodels

import androidx.lifecycle.ViewModel
import com.gksenon.sleepdiary.domain.Diary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import java.util.Date
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SleepDiaryViewModel @Inject constructor(diary: Diary) :
    ViewModel() {

    val sleepDiary: Flow<List<Day>> = diary.getSleepDiary()
        .map { diary -> diary.sortedByDescending { it.start } }
        .map { diary ->
            val eventsMap = mutableMapOf<Long, MutableList<DiaryEvent>>()
            diary.forEachIndexed { index, sleep ->
                val nextSleepStart = if (index == 0) Date() else diary[index - 1].start
                val awakeDuration = (nextSleepStart.time - sleep.end.time).milliseconds
                val awake = DiaryEvent.Awake(duration = awakeDuration)

                val started = DiaryEvent.SleepStarted(sleepId = sleep.id, date = sleep.start)

                val sleepDuration = (sleep.end.time - sleep.start.time).milliseconds
                val finished = DiaryEvent.SleepFinished(
                    sleepId = sleep.id,
                    date = sleep.end,
                    duration = sleepDuration
                )

                with(eventsMap.getOrPut(getDayTimestamp(sleep.end)) { mutableListOf() }) {
                    add(awake)
                    add(finished)
                }
                with(eventsMap.getOrPut(getDayTimestamp(sleep.start)) { mutableListOf() }) { add(started) }
            }
            eventsMap.map { Day(Date(it.key), it.value) }
        }

    private fun getDayTimestamp(date: Date): Long {
        val dateTimeCalendar = Calendar.getInstance().apply { time = date }
        val dateCalendar = Calendar.getInstance().apply { clear() }
        dateCalendar.set(Calendar.YEAR, dateTimeCalendar.get(Calendar.YEAR))
        dateCalendar.set(Calendar.MONTH, dateTimeCalendar.get(Calendar.MONTH))
        dateCalendar.set(Calendar.DAY_OF_MONTH, dateTimeCalendar.get(Calendar.DAY_OF_MONTH))
        return dateCalendar.timeInMillis
    }
}

data class Day(val date: Date, val events: List<DiaryEvent>)

sealed class DiaryEvent {

    data class Awake(val duration: Duration) : DiaryEvent()

    data class SleepStarted(val sleepId: UUID, val date: Date) : DiaryEvent()

    data class SleepFinished(val sleepId: UUID, val date: Date, val duration: Duration) : DiaryEvent()
}