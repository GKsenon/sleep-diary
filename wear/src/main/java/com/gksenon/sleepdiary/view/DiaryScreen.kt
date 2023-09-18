package com.gksenon.sleepdiary.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.gksenon.sleepdiary.R
import com.gksenon.sleepdiary.viewmodel.DiaryEntry
import com.gksenon.sleepdiary.viewmodel.DiaryState
import com.gksenon.sleepdiary.viewmodel.DiaryViewModel
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.composables.ProgressIndicatorSegment
import com.google.android.horologist.composables.SegmentedProgressIndicator
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun DiaryScreen(
    viewModel: DiaryViewModel = hiltViewModel(),
    onTrackButtonClicked: () -> Unit,
    onAddButtonClicked: () -> Unit
) {
    val state: DiaryState by viewModel.state.collectAsState(DiaryState(0.milliseconds, 0.milliseconds, emptyList()))

    Box(modifier = Modifier.fillMaxSize()) {
        SleepProgressIndicator(diary = state.diaryForToday)
        DailyStats(
            timeAwake = state.timeAwake,
            timeAsleep = state.timeAsleep,
            onAddButtonClicked = onAddButtonClicked,
            onTrackButtonClicked = onTrackButtonClicked
        )
    }
}

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun SleepProgressIndicator(diary: List<DiaryEntry>) {
    val trackSegments = buildList {
        val midnight = Instant.now().atZone(ZoneId.systemDefault()).truncatedTo(ChronoUnit.DAYS).toInstant()

        for (i in 1 until diary.size) {
            val previousEntry = diary[i - 1]
            val currentEntry = diary[i]
            val weight = previousEntry.instant.until(currentEntry.instant, ChronoUnit.SECONDS).toFloat()
            val color = when (currentEntry) {
                is DiaryEntry.Awake -> MaterialTheme.colors.primary
                is DiaryEntry.Asleep -> MaterialTheme.colors.secondary
            }
            add(ProgressIndicatorSegment(weight = weight, indicatorColor = color))
        }

        if (diary.isNotEmpty()) {
            val weight = diary.last().instant.until(Instant.now(), ChronoUnit.SECONDS).toFloat()
            add(ProgressIndicatorSegment(weight = weight, indicatorColor = MaterialTheme.colors.secondary))
        } else {
            val weight = midnight.until(Instant.now(), ChronoUnit.SECONDS).toFloat()
            add(ProgressIndicatorSegment(weight = weight, indicatorColor = MaterialTheme.colors.secondary))
        }

        val emptySegmentWeight = (86400 - midnight.until(Instant.now(), ChronoUnit.SECONDS)).toFloat()
        val emptySegmentColor = MaterialTheme.colors.onBackground.copy(alpha = 0.1f)
        add(ProgressIndicatorSegment(weight = emptySegmentWeight, indicatorColor = emptySegmentColor))
    }

    SegmentedProgressIndicator(trackSegments = trackSegments, progress = 1f)
}

@Composable
fun DailyStats(
    timeAwake: Duration,
    timeAsleep: Duration,
    onAddButtonClicked: () -> Unit,
    onTrackButtonClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val dateFormatter = SimpleDateFormat.getDateInstance()
        Text(text = dateFormatter.format(Date()))

        timeAsleep.toComponents { hours, minutes, _, _ ->
            Text(text = stringResource(R.string.time_asleep, hours, minutes))
        }
        timeAwake.toComponents { hours, minutes, _, _ ->
            Text(text = stringResource(R.string.time_awake, hours, minutes))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = onAddButtonClicked, colors = ButtonDefaults.secondaryButtonColors()) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = stringResource(id = R.string.add_sleep)
                )
            }
            Button(onClick = onTrackButtonClicked) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_play),
                    contentDescription = stringResource(id = R.string.track_sleep)
                )
            }
        }
    }
}