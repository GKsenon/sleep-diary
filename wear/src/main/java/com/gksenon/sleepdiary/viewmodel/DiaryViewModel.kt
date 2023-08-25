package com.gksenon.sleepdiary.viewmodel

import androidx.lifecycle.ViewModel
import com.gksenon.sleepdiary.domain.Diary
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class DiaryViewModel @Inject constructor(diary: Diary) : ViewModel() {

    val state: Flow<DiaryState> = diary.getSleepDiary().map { DiaryState(0.milliseconds, 0.milliseconds, emptyList()) }
}

data class DiaryState(val timeAsleep: Duration, val timeAwake: Duration, val diaryForToday: List<DiaryEntry>)

sealed class DiaryEntry {
    data class Asleep(val instant: Instant) : DiaryEntry()

    data class Awake(val instant: Instant) : DiaryEntry()
}
