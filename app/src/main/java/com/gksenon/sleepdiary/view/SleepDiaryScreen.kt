package com.gksenon.sleepdiary.view

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gksenon.sleepdiary.R
import com.gksenon.sleepdiary.data.Sleep
import com.gksenon.sleepdiary.viewmodels.SleepDiaryViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat

@Composable
fun SleepDiaryScreen(
    viewModel: SleepDiaryViewModel = hiltViewModel(),
    onNavigateToSleepCreation: () -> Unit,
    onNavigateToSleepTracking: () -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(title = {
            Text(stringResource(R.string.app_name))
        })
    },
    floatingActionButton = {
        FloatingActionButton(onClick = { onNavigateToSleepCreation() }) {
            Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_sleep))
        }
//        FloatingActionButton(onClick = { onNavigateToSleepTracking() }) {
//            Icon(Icons.Filled.PlayArrow, contentDescription = stringResource(R.string.track_sleep))
//        }
    }) { contentPadding ->
        val sleepDiary: List<Sleep>? by viewModel.sleepDiary.observeAsState()

        LazyColumn(
            modifier = Modifier.padding(
                start = 16.dp,
                top = contentPadding.calculateTopPadding(),
                end = 16.dp,
                bottom = contentPadding.calculateBottomPadding()
            )
        ) {
            items(items = sleepDiary ?: emptyList(), key = { sleep -> sleep.id }, itemContent = { sleep ->
                SleepDiaryEntry(sleep)
            })
        }
    }
}

@Composable
fun SleepDiaryEntry(sleep: Sleep) {
    val dateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT)
    val start = dateFormat.format(sleep.start)
    val end = dateFormat.format(sleep.end)
    Text(
        text = stringResource(R.string.sleep_time_range, start, end),
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
    )
}