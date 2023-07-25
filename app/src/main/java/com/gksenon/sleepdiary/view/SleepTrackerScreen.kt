package com.gksenon.sleepdiary.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gksenon.sleepdiary.R
import com.gksenon.sleepdiary.view.utils.Chronometer
import com.gksenon.sleepdiary.view.utils.DateVisualTransformation
import com.gksenon.sleepdiary.view.utils.TimeVisualTransformation
import com.gksenon.sleepdiary.viewmodels.SleepTrackerViewModel
import com.gksenon.sleepdiary.viewmodels.TrackerState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
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
                is TrackerState.Stopped -> StoppedScreen(onStart = { viewModel.startTracking() })
                is TrackerState.Tracking -> TrackingScreen(
                    state = state as TrackerState.Tracking,
                    onStop = { viewModel.stopTracking() },
                    onEditStartDate = { viewModel.editStartDate() },
                    onStartDateChanged = { viewModel.onStartDateChanged(it) },
                    onSaveStartDate = { viewModel.saveStartDate() },
                    onEditStartTime = { viewModel.editStartTime() },
                    onStartTimeChanged = { viewModel.onStartTimeChanged(it) },
                    onSaveStartTime = { viewModel.saveStartTime() }
                )
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                val notificationPermissionState =
                    rememberPermissionState(permission = android.Manifest.permission.POST_NOTIFICATIONS)
                if (!notificationPermissionState.status.isGranted) {
                    if (notificationPermissionState.status.shouldShowRationale) {
                        NotificationPermissionRationale { notificationPermissionState.launchPermissionRequest() }
                    } else {
                        LaunchedEffect(notificationPermissionState) {
                            notificationPermissionState.launchPermissionRequest()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StoppedScreen(onStart: () -> Unit) {
    ElevatedButton(
        onClick = onStart,
        modifier = Modifier
            .width(160.dp)
            .height(160.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = stringResource(R.string.start_tracking),
                modifier = Modifier.size(64.dp)
            )
            Text(text = stringResource(R.string.start_tracking))
        }
    }
}

@Composable
fun TrackingScreen(
    state: TrackerState.Tracking,
    onStop: () -> Unit,
    onEditStartDate: () -> Unit,
    onStartDateChanged: (String) -> Unit,
    onSaveStartDate: () -> Unit,
    onEditStartTime: () -> Unit,
    onStartTimeChanged: (String) -> Unit,
    onSaveStartTime: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Chronometer(base = state.startDate.time)
        ElevatedButton(
            onClick = onStop,
            modifier = Modifier
                .width(160.dp)
                .height(160.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    painter = painterResource(R.drawable.ic_stop),
                    contentDescription = stringResource(R.string.stop_tracking),
                    modifier = Modifier.size(64.dp)
                )
                Text(text = stringResource(R.string.stop_tracking), fontSize = 18.sp)
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.formattedStartDate,
                onValueChange = onStartDateChanged,
                enabled = state.isStartDateEditingEnabled,
                label = { Text(stringResource(R.string.start_date)) },
                supportingText = {
                    if (state.showStartDateInFutureError) {
                        Text(stringResource(R.string.date_in_future_error))
                    } else if (state.showStartDateInvalidFormatError)
                        Text(stringResource(R.string.date_invalid_format_error))
                    else
                        Text(stringResource(R.string.date_supporting_text))
                },
                trailingIcon = {
                    if (state.showStartDateInvalidFormatError) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_error),
                            contentDescription = stringResource(R.string.save_start_date),
                            modifier = Modifier.size(24.dp)
                        )
                    } else if (state.isStartDateEditingEnabled) {
                        IconButton(onClick = onSaveStartDate) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = stringResource(R.string.save_start_date)
                            )
                        }
                    } else {
                        IconButton(onClick = onEditStartDate) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = stringResource(R.string.edit_start_date)
                            )
                        }
                    }
                },
                isError = state.showStartDateInFutureError || state.showStartDateInvalidFormatError,
                visualTransformation = DateVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = state.formattedStartTime,
                onValueChange = onStartTimeChanged,
                enabled = state.isStartTimeEditingEnabled,
                label = { Text(stringResource(R.string.start_time)) },
                supportingText = {
                    if (state.showStartTimeInFutureError) {
                        Text(stringResource(R.string.time_in_future_error))
                    } else if (state.showStartTimeInvalidFormatError)
                        Text(stringResource(R.string.time_invalid_format_error))
                    else
                        Text(stringResource(R.string.time_supporting_text))
                },
                trailingIcon = {
                    if (state.showStartTimeInvalidFormatError) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_error),
                            contentDescription = stringResource(R.string.save_start_date),
                            modifier = Modifier.size(24.dp)
                        )
                    } else if (state.isStartTimeEditingEnabled) {
                        IconButton(onClick = onSaveStartTime) {
                            Icon(
                                imageVector = Icons.Outlined.Check,
                                contentDescription = stringResource(R.string.save_start_date)
                            )
                        }
                    } else {
                        IconButton(onClick = onEditStartTime) {
                            Icon(
                                imageVector = Icons.Outlined.Edit,
                                contentDescription = stringResource(R.string.save_start_time)
                            )
                        }
                    }
                },
                isError = state.showStartTimeInFutureError || state.showStartTimeInvalidFormatError,
                visualTransformation = TimeVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun NotificationPermissionRationale(onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(id = R.string.notification_permission)) },
        text = { Text(text = stringResource(id = R.string.notification_permission_rationale)) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            Button(onClick = {}) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        }
    )
}