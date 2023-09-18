package com.gksenon.sleepdiary.viewmodel

import androidx.lifecycle.ViewModel
import com.gksenon.sleepdiary.domain.Diary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class DiaryViewModel @Inject constructor(diary: Diary) : ViewModel() {

    val state: Flow<DiaryState> = diary.getSleepDiary().map { diary ->
        val midnight = Instant.now().atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS).toInstant()

        val entries = diary
            .filter { sleep ->
                !sleep.start.toInstant().isBefore(midnight) || !sleep.end.toInstant().isBefore(midnight)
            }
            .sortedBy { it.start }
            .flatMapIndexed { index, sleep ->
                if (index == 0) {
                    val list = mutableListOf<DiaryEntry>()

                    if (sleep.start.toInstant().isBefore(midnight)) {
                        list.add(DiaryEntry.Asleep(midnight))
                    } else {
                        list.add(DiaryEntry.Awake(midnight))
                        list.add(DiaryEntry.Asleep(sleep.start.toInstant()))
                    }

                    list.add(DiaryEntry.Awake(sleep.end.toInstant()))

                    list
                } else {
                    listOf(DiaryEntry.Asleep(sleep.start.toInstant()), DiaryEntry.Awake(sleep.end.toInstant()))
                }
            }.ifEmpty { listOf(DiaryEntry.Awake(midnight)) }

        val timeAsleep = entries.foldIndexed(0L) { index, acc, entry ->
            if (entry is DiaryEntry.Awake && index != 0)
                acc + entries[index - 1].instant.until(entry.instant, ChronoUnit.SECONDS)
            else
                acc
        }

        val timeAwake = entries.foldIndexed(0L) { index, acc, entry ->
            if (entry is DiaryEntry.Asleep && index != 0)
                acc + entries[index - 1].instant.until(entry.instant, ChronoUnit.SECONDS)
            else if (entry is DiaryEntry.Awake && index == entries.lastIndex)
                acc + entry.instant.until(Instant.now(), ChronoUnit.SECONDS)
            else
                acc
        }

        DiaryState(timeAsleep.seconds, timeAwake.seconds, entries)
    }
}

data class DiaryState(val timeAsleep: Duration, val timeAwake: Duration, val diaryForToday: List<DiaryEntry>)

sealed class DiaryEntry(val instant: Instant) {
    class Asleep(instant: Instant) : DiaryEntry(instant)

    class Awake(instant: Instant) : DiaryEntry(instant)
}
