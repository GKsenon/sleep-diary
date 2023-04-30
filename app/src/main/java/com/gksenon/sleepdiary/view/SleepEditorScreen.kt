package com.gksenon.sleepdiary.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gksenon.sleepdiary.R
import com.gksenon.sleepdiary.view.utils.DateVisualTransformation
import com.gksenon.sleepdiary.view.utils.TimeVisualTransformation
import com.gksenon.sleepdiary.viewmodels.SleepEditorViewModel

@Composable
fun SleepEditorScreen(
    viewModel: SleepEditorViewModel = hiltViewModel(),
    onSaveSleep: () -> Unit
) {
    val state by viewModel.sleepEditorState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(stringResource(R.string.add_sleep)) },
            actions = {
                IconButton(onClick = { viewModel.saveSleep(onSaveSleep) }) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                }
            })
    }) { contentPadding ->
        Column(
            modifier = Modifier.padding(
                top = contentPadding.calculateTopPadding() + 16.dp,
                start = 16.dp,
                bottom = contentPadding.calculateBottomPadding() + 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.startDate,
                    onValueChange = { viewModel.onStartDateChanged(it) },
                    label = { Text(stringResource(R.string.start_date)) },
                    supportingText = {
                        if (state.showStartDateError)
                            Text(stringResource(R.string.date_invalid_format_error))
                        else
                            Text(stringResource(R.string.date_supporting_text))
                    },
                    isError = state.showStartDateError,
                    singleLine = true,
                    visualTransformation = DateVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.startTime,
                    onValueChange = { viewModel.onStartTimeChanged(it) },
                    label = { Text(stringResource(R.string.start_time)) },
                    supportingText = {
                        if (state.showStartTimeError)
                            Text(stringResource(R.string.time_invalid_format_error))
                        else
                            Text(stringResource(R.string.time_supporting_text))
                    },
                    isError = state.showStartTimeError,
                    singleLine = true,
                    visualTransformation = TimeVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.endDate,
                    onValueChange = { viewModel.onEndDateChanged(it) },
                    label = { Text(stringResource(R.string.end_date)) },
                    supportingText = {
                        if (state.showEndDateError)
                            Text(stringResource(R.string.date_invalid_format_error))
                        else
                            Text(stringResource(R.string.date_supporting_text))
                    },
                    isError = state.showEndDateError,
                    singleLine = true,
                    visualTransformation = DateVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.endTime,
                    onValueChange = { viewModel.onEndTimeChanged(it) },
                    label = { Text(stringResource(R.string.end_time)) },
                    supportingText = {
                        if (state.showEndTimeError)
                            Text(stringResource(R.string.time_invalid_format_error))
                        else
                            Text(stringResource(R.string.time_supporting_text))
                    },
                    isError = state.showEndTimeError,
                    singleLine = true,
                    visualTransformation = TimeVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
