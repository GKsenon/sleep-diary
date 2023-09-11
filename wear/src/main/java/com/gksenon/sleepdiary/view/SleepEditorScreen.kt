package com.gksenon.sleepdiary.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.gksenon.sleepdiary.R
import com.gksenon.sleepdiary.viewmodel.SleepEditorViewModel
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

@Composable
fun SleepEditorScreen(
    viewModel: SleepEditorViewModel = hiltViewModel(),
    start: Instant,
    end: Instant,
    showStartDateTimePicker: () -> Unit,
    showEndDateTimePicker: () -> Unit,
    closeSleepEditor: () -> Unit
) {
    val dateTimeFormatter = SimpleDateFormat.getDateTimeInstance()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier
            .background(color = MaterialTheme.colors.primary, shape = CircleShape)
            .padding(8.dp)
            .clickable { showStartDateTimePicker() }
        ) {
            Text(
                text = stringResource(id = R.string.start_date),
                color = MaterialTheme.colors.onPrimary,
                fontSize = 12.sp
            )
            Text(
                text = dateTimeFormatter.format(Date.from(start)),
                color = MaterialTheme.colors.onPrimary,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier
            .background(color = MaterialTheme.colors.primary, shape = CircleShape)
            .padding(8.dp)
            .clickable { showEndDateTimePicker() }
        ) {
            Text(
                text = stringResource(id = R.string.end_date),
                color = MaterialTheme.colors.onPrimary,
                fontSize = 12.sp
            )
            Text(
                text = dateTimeFormatter.format(Date.from(end)),
                color = MaterialTheme.colors.onPrimary,
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.onSaveButtonClicked(start = start, end = end, onSleepSaved = closeSleepEditor) }) {
            Icon(
                painter = painterResource(id = R.drawable.ic_save),
                contentDescription = stringResource(id = R.string.save_sleep)
            )
        }
    }
}