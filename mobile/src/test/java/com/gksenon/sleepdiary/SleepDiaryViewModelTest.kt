package com.gksenon.sleepdiary

import com.gksenon.sleepdiary.domain.Diary
import com.gksenon.sleepdiary.domain.Sleep
import com.gksenon.sleepdiary.viewmodels.DiaryEvent
import com.gksenon.sleepdiary.viewmodels.SleepDiaryViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.UUID
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class SleepDiaryViewModelTest {

    @Test
    fun sleepDiaryViewModel_emptyDiary_givesEmptyState() = runTest {
        val diary = mockk<Diary> {
            every { getSleepDiary() } returns emptyFlow()
        }
        val viewModel = SleepDiaryViewModel(diary)

        val state = viewModel.sleepDiary.lastOrNull()

        assertTrue(state.isNullOrEmpty())
    }

    @Test
    fun sleepDiaryViewModel_unsortedDiary_givesGroupedDiaryEvents() = runTest {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val diaryData = listOf(
            Sleep(
                id = UUID.randomUUID(),
                start = dateFormat.parse("20.03.2023 12:00")!!,
                end = dateFormat.parse("20.03.2023 14:00")!!
            ),
            Sleep(
                id = UUID.randomUUID(),
                start = dateFormat.parse("20.03.2023 20:15")!!,
                end = dateFormat.parse("21.03.2023 07:30")!!
            ),
            Sleep(
                id = UUID.randomUUID(),
                start = dateFormat.parse("21.03.2023 11:30")!!,
                end = dateFormat.parse("21.03.2023 13:00")!!
            )
        )

        val diary = mockk<Diary> {
            every { getSleepDiary() } returns flowOf(diaryData)
        }
        val viewModel = SleepDiaryViewModel(diary)

        val state = viewModel.sleepDiary.last()

        assertEquals(2, state.size)
        assertEquals(dateFormat.parse("21.03.2023 00:00"), state[0].date)
        assertEquals(dateFormat.parse("20.03.2023 00:00"), state[1].date)

        assertEquals(5, state[0].events.size)
        println(state[0].events)
        println(state[1].events)
        val awakeDuration = diaryData[2].end.toInstant().until(Instant.now(), ChronoUnit.SECONDS).seconds
        assertEquals(awakeDuration.inWholeMinutes, (state[0].events[0] as DiaryEvent.Awake).duration.inWholeMinutes)
        assertEquals(diaryData[2].toSleepFinishedEvent(), state[0].events[1])
        assertEquals(diaryData[2].toSleepStartedEvent(), state[0].events[2])
        assertEquals(DiaryEvent.Awake(4.hours), state[0].events[3])
        assertEquals(diaryData[1].toSleepFinishedEvent(), state[0].events[4])

        assertEquals(4, state[1].events.size)
        assertEquals(diaryData[1].toSleepStartedEvent(), state[1].events[0])
        assertEquals(DiaryEvent.Awake(375.minutes), state[1].events[1])
        assertEquals(diaryData[0].toSleepFinishedEvent(), state[1].events[2])
        assertEquals(diaryData[0].toSleepStartedEvent(), state[1].events[3])
    }

    private fun Sleep.toSleepStartedEvent() = DiaryEvent.SleepStarted(id, start)

    private fun Sleep.toSleepFinishedEvent() = DiaryEvent.SleepFinished(id, end, (end.time - start.time).milliseconds)
}