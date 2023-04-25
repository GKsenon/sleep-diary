package com.gksenon.sleepdiary.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gksenon.sleepdiary.R
import com.gksenon.sleepdiary.viewmodels.SleepTrackerViewModel
import com.gksenon.sleepdiary.viewmodels.TrackerState
import kotlin.time.Duration

@Composable
fun SleepTrackerScreen(
    viewModel: SleepTrackerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.track_sleep)) })
    }) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (state) {
                is TrackerState.Stopped -> StoppedScreen { viewModel.startTracking() }
                is TrackerState.Tracking -> TrackingScreen(duration = (state as TrackerState.Tracking).duration) { viewModel.stopTracking() }
            }
        }
    }
}

@Composable
fun StoppedScreen(onClick: () -> Unit) {
    ElevatedButton(
        onClick = onClick,
        modifier = Modifier
            .width(180.dp)
            .height(180.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.PlayArrow,
            contentDescription = stringResource(R.string.start_tracking),
            modifier = Modifier.size(64.dp)
        )
    }
}

@Composable
fun TrackingScreen(duration: Duration, onClick: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            duration.toComponents { hours, minutes, seconds, _ ->
                Text(
                    text = "%02d : %02d : %02d".format(hours, minutes, seconds),
                    fontSize = 48.sp
                )
            }
        }
        ElevatedButton(
            onClick = onClick,
            modifier = Modifier
                .width(180.dp)
                .height(180.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_stop),
                contentDescription = stringResource(R.string.stop_tracking),
                modifier = Modifier.size(64.dp)
            )
        }
    }
}