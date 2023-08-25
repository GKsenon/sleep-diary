package com.gksenon.sleepdiary.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import com.gksenon.sleepdiary.R
import com.gksenon.sleepdiary.domain.Tracker
import com.gksenon.sleepdiary.view.utils.Chronometer
import com.gksenon.sleepdiary.viewmodel.TrackerViewModel

@Composable
fun TrackerScreen(
    viewModel: TrackerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState(initial = Tracker.Event.Stopped)
    when(state) {
        is Tracker.Event.Started -> StartedScreen(event = state as Tracker.Event.Started, onStop = { viewModel.stop() })
        is Tracker.Event.Stopped -> StoppedScreen(onStart = { viewModel.start() })
    }
}

@Composable
fun StartedScreen(
    event: Tracker.Event.Started,
    onStop: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Chronometer(base = event.start.time)
        Button(onClick = onStop) {
            Icon(
                painter = painterResource(id = R.drawable.ic_stop),
                contentDescription = stringResource(id = R.string.stop_tracking))
        }
    }
}

@Composable
fun StoppedScreen(onStart: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Button(onClick = onStart) {
            Icon(
                painter = painterResource(id = R.drawable.ic_play),
                contentDescription = stringResource(id = R.string.start_tracking)
            )
        }
    }
}