package com.gksenon.sleepdiary.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.gksenon.sleepdiary.R
import com.gksenon.sleepdiary.viewmodel.DiaryState
import com.gksenon.sleepdiary.viewmodel.DiaryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun DiaryScreen(
    viewModel: DiaryViewModel = hiltViewModel(),
    onTrackButtonClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val state: DiaryState by viewModel.state.collectAsState(DiaryState(0.milliseconds, 0.milliseconds, emptyList()))
        
        val dateFormatter = SimpleDateFormat.getDateInstance()
        Text(text = dateFormatter.format(Date()))
        
        state.timeAsleep.toComponents { hours, minutes, _, _ ->
            Text(text = stringResource(R.string.time_asleep, hours, minutes))
        }
        state.timeAwake.toComponents { hours, minutes, _, _ ->
            Text(text = stringResource(R.string.time_awake, hours, minutes))
        }
        Button(onClick = { onTrackButtonClicked() }) {
            Text(text = "Track")
        }
    }
}