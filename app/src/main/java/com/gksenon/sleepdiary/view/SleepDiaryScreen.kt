package com.gksenon.sleepdiary.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gksenon.sleepdiary.R
import com.gksenon.sleepdiary.viewmodels.Day
import com.gksenon.sleepdiary.viewmodels.DiaryEvent
import com.gksenon.sleepdiary.viewmodels.SleepDiaryViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

@Composable
fun SleepDiaryScreen(
    viewModel: SleepDiaryViewModel = hiltViewModel(),
    onNavigateToSleepEditor: () -> Unit,
    onNavigateToSleepTracking: () -> Unit,
    onSleepClicked: (UUID) -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(stringResource(R.string.app_name))
        })
    },
        floatingActionButton = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                SmallFloatingActionButton(
                    onClick = { onNavigateToSleepEditor() },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = stringResource(R.string.add_sleep))
                }
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(R.string.track_sleep_button_text)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = stringResource(R.string.track_sleep)
                        )
                    },
                    onClick = { onNavigateToSleepTracking() },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }) { contentPadding ->

        val sleepDiary: List<Day> by viewModel.sleepDiary.collectAsState(emptyList())

        LazyColumn(contentPadding = contentPadding) {
            items(
                items = sleepDiary,
                itemContent = { day ->
                    Day(
                        day = day,
                        onSleepClicked = onSleepClicked,
                        onDeleteSleep = { viewModel.deleteSleep(it) }
                    )
                }
            )
        }
    }
}

@Composable
fun Day(
    day: Day,
    onSleepClicked: (UUID) -> Unit,
    onDeleteSleep: (UUID) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
    Box {
        Column {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.outline)
                    .fillMaxWidth()
                    .height(1.dp)
            )
            day.events.forEach { event ->
                when (event) {
                    is DiaryEvent.Awake -> AwakeEvent(event)
                    is DiaryEvent.SleepFinished -> SleepFinishedEvent(event, onSleepClicked, onDeleteSleep)
                    is DiaryEvent.SleepStarted -> SleepStartedEvent(event, onSleepClicked)
                }
            }
        }
        Text(
            text = dateFormat.format(day.date),
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 1.dp)
                .background(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(4.dp))
                .padding(12.dp)
        )
    }
}

@Composable
fun AwakeEvent(event: DiaryEvent.Awake) {
    Text(
        text = stringResource(id = R.string.awake, event.duration.inWholeHours, event.duration.inWholeMinutes % 60),
        fontSize = 18.sp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 80.dp, top = 12.dp, end = 12.dp, bottom = 12.dp)
    )
}

@Composable
fun SleepFinishedEvent(
    event: DiaryEvent.SleepFinished,
    onSleepClicked: (UUID) -> Unit,
    onDeleteSleep: (UUID) -> Unit
) {
    Row(
        modifier = Modifier
            .clickable { onSleepClicked(event.sleepId) }
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(start = 80.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        Text(
            text = stringResource(
                id = R.string.sleep_finished,
                timeFormat.format(event.date), event.duration.inWholeHours, event.duration.inWholeMinutes % 60
            ),
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { onDeleteSleep(event.sleepId) }) {
            Icon(
                imageVector = Icons.Filled.Delete,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                contentDescription = stringResource(id = R.string.delete_sleep)
            )
        }
    }
}

@Composable
fun SleepStartedEvent(
    event: DiaryEvent.SleepStarted,
    onSleepClicked: (UUID) -> Unit
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    Text(
        text = stringResource(id = R.string.sleep_started, timeFormat.format(event.date)),
        fontSize = 18.sp,
        modifier = Modifier
            .clickable { onSleepClicked(event.sleepId) }
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(start = 80.dp, top = 12.dp, end = 12.dp, bottom = 12.dp)
    )
}
