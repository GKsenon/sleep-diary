package com.gksenon.sleepdiary.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gksenon.sleepdiary.R
import com.gksenon.sleepdiary.viewmodels.SleepDiaryViewModel
import com.gksenon.sleepdiary.viewmodels.SleepState
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun SleepDiaryScreen(
    viewModel: SleepDiaryViewModel = hiltViewModel(),
    onNavigateToSleepEditor: () -> Unit,
    onNavigateToSleepTracking: () -> Unit
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
                    Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_sleep))
                }
                ExtendedFloatingActionButton(
                    text = { Text(text = stringResource(R.string.track_sleep_button_text)) },
                    icon = {
                        Icon(
                            Icons.Filled.PlayArrow,
                            contentDescription = stringResource(R.string.track_sleep)
                        )
                    },
                    onClick = { onNavigateToSleepTracking() })
            }
        }) { contentPadding ->

        val sleepDiary: Map<String, List<SleepState>> by viewModel.sleepDiary.collectAsState(
            emptyMap()
        )

        LazyColumn(
            contentPadding = contentPadding,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            sleepDiary.forEach { entry ->
                item {
                    Text(
                        text = entry.key,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(
                    items = entry.value,
                    key = { sleep -> sleep.id },
                    itemContent = { sleep ->
                        SleepDiaryEntry(sleep)
                    })
            }
        }
    }
}

@Composable
fun SleepDiaryEntry(sleep: SleepState) {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val start = dateFormat.format(sleep.start)
    val end = dateFormat.format(sleep.end)

    Column(modifier = Modifier.padding(8.dp)) {
        Row(
            modifier = Modifier
                .height(64.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
            Text(
                text = stringResource(
                    R.string.wake_time,
                    sleep.wakeDuration.inWholeHours,
                    sleep.wakeDuration.inWholeMinutes % 60
                ),
                modifier = Modifier.padding(8.dp)
            )
        }
        Text(
            text = stringResource(
                R.string.sleep_time_range,
                start,
                end,
                sleep.sleepDuration.inWholeHours,
                sleep.sleepDuration.inWholeMinutes % 60
            ),
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}