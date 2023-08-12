package com.gksenon.sleepdiary.view.utils

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun Chronometer(base: Long) {
    var duration by remember { mutableStateOf(0.milliseconds) }

    LaunchedEffect(base) {
        duration = (System.currentTimeMillis() - base).milliseconds
        while (true) {
            delay(1000L)
            duration = duration.plus(1.seconds)
        }
    }

    duration.toComponents { hours, minutes, seconds, _ ->
        Text(
            text = "%02d : %02d : %02d".format(hours, minutes, seconds),
            fontSize = 32.sp
        )
    }
}