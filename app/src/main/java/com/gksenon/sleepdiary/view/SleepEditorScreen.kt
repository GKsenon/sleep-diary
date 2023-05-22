package com.gksenon.sleepdiary.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.gksenon.sleepdiary.viewmodels.ValidationStatus

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
                    value = state.startDateTextFieldState.value,
                    onValueChange = { viewModel.onStartDateChanged(it) },
                    label = { Text(stringResource(R.string.start_date)) },
                    supportingText = {
                        when (state.startDateTextFieldState.validationStatus) {
                            ValidationStatus.VALID -> Text(stringResource(R.string.date_supporting_text))
                            ValidationStatus.INVALID_FORMAT -> Text(stringResource(R.string.date_invalid_format_error))
                            ValidationStatus.VALUE_IN_FUTURE -> Text(stringResource(R.string.date_in_future_error))
                        }
                    },
                    isError = state.startDateTextFieldState.validationStatus != ValidationStatus.VALID,
                    singleLine = true,
                    visualTransformation = DateVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.startTimeTextFieldState.value,
                    onValueChange = { viewModel.onStartTimeChanged(it) },
                    label = { Text(stringResource(R.string.start_time)) },
                    supportingText = {
                        when (state.startTimeTextFieldState.validationStatus) {
                            ValidationStatus.VALID -> Text(stringResource(R.string.time_supporting_text))
                            ValidationStatus.INVALID_FORMAT -> Text(stringResource(R.string.time_invalid_format_error))
                            ValidationStatus.VALUE_IN_FUTURE -> Text(stringResource(R.string.time_in_future_error))
                        }
                    },
                    isError = state.startTimeTextFieldState.validationStatus != ValidationStatus.VALID,
                    singleLine = true,
                    visualTransformation = TimeVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.endDateTextFieldState.value,
                    onValueChange = { viewModel.onEndDateChanged(it) },
                    label = { Text(stringResource(R.string.end_date)) },
                    supportingText = {
                        when (state.endDateTextFieldState.validationStatus) {
                            ValidationStatus.VALID -> Text(stringResource(R.string.date_supporting_text))
                            ValidationStatus.INVALID_FORMAT -> Text(stringResource(R.string.date_invalid_format_error))
                            ValidationStatus.VALUE_IN_FUTURE -> Text(stringResource(R.string.date_in_future_error))
                        }
                    },
                    isError = state.endDateTextFieldState.validationStatus != ValidationStatus.VALID,
                    singleLine = true,
                    visualTransformation = DateVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.endTimeTextFieldState.value,
                    onValueChange = { viewModel.onEndTimeChanged(it) },
                    label = { Text(stringResource(R.string.end_time)) },
                    supportingText = {
                        when (state.endTimeTextFieldState.validationStatus) {
                            ValidationStatus.VALID -> Text(stringResource(R.string.time_supporting_text))
                            ValidationStatus.INVALID_FORMAT -> Text(stringResource(R.string.time_invalid_format_error))
                            ValidationStatus.VALUE_IN_FUTURE -> Text(stringResource(R.string.time_in_future_error))
                        }
                    },
                    isError = state.endTimeTextFieldState.validationStatus != ValidationStatus.VALID,
                    singleLine = true,
                    visualTransformation = TimeVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
