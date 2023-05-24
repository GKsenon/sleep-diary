package com.gksenon.sleepdiary.viewmodels

import androidx.lifecycle.ViewModel
import com.gksenon.sleepdiary.data.Sleep
import com.gksenon.sleepdiary.data.SleepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SleepDiaryViewModel @Inject constructor(sleepRepository: SleepRepository) :
    ViewModel() {

    private val dateFormat = SimpleDateFormat("d MMMM, EEEE", Locale.getDefault())

    val sleepDiary: Flow<Map<String, List<SleepState>>> = sleepRepository.getSleepDiary()
        .map { sleepDiary ->
            sleepDiary.sortedByDescending { it.start }
                .mapIndexed { index, sleep ->
                    val sleepDuration = sleep.end.time.milliseconds - sleep.start.time.milliseconds

                    val startOfNextSleep = if (index == 0) Date() else sleepDiary[index - 1].start
                    val wakeDuration =
                        startOfNextSleep.time.milliseconds - sleep.end.time.milliseconds

                    SleepState(sleep.id, sleep.start, sleep.end, sleepDuration, wakeDuration)
                }.groupBy { dateFormat.format(it.start) }
        }

    fun onSleepClicked(sleepId: UUID) {

    }
}

data class SleepState(
    val id: UUID,
    val start: Date,
    val end: Date,
    val sleepDuration: Duration,
    val wakeDuration: Duration
)