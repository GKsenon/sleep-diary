package com.gksenon.sleepdiary.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gksenon.sleepdiary.R
import com.gksenon.sleepdiary.viewmodels.SleepCreationViewModel

@Composable
fun SleepCreationScreen(
    viewModel: SleepCreationViewModel = hiltViewModel(),
    onSleepSaved: () -> Unit
) {
    var startDate by rememberSaveable { mutableStateOf("") }
    var startTime by rememberSaveable { mutableStateOf("") }
    var endDate by rememberSaveable { mutableStateOf("") }
    var endTime by rememberSaveable { mutableStateOf("") }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(stringResource(R.string.add_sleep)) },
            actions = {
                IconButton(onClick = {
                    viewModel.saveSleep(startDate, startTime, endDate, endTime)
                    onSleepSaved()
                }) {
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
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text(stringResource(R.string.start_date)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text(stringResource(R.string.start_time)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text(stringResource(R.string.end_date)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text(stringResource(R.string.end_time)) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}