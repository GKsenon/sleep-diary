package com.gksenon.sleepdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.gksenon.sleepdiary.theme.SleepDiaryTheme
import com.gksenon.sleepdiary.view.DiaryScreen
import com.gksenon.sleepdiary.view.SleepEditorScreen
import com.gksenon.sleepdiary.view.TrackerScreen
import com.google.android.horologist.composables.DatePicker
import com.google.android.horologist.composables.TimePicker
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SleepDiaryTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController = rememberSwipeDismissableNavController()) {
    var start: ZonedDateTime by remember { mutableStateOf(Instant.now().atZone(ZoneId.systemDefault())) }
    var end: ZonedDateTime by remember { mutableStateOf(Instant.now().atZone(ZoneId.systemDefault())) }

    SwipeDismissableNavHost(navController = navController, startDestination = Screen.Diary.route) {
        composable(Screen.Diary.route) {
            DiaryScreen(
                onTrackButtonClicked = { navController.navigate(Screen.Tracker.route) },
                onAddButtonClicked = { navController.navigate(Screen.Editor.route) }
            )
        }
        composable(Screen.Tracker.route) { TrackerScreen() }
        composable(Screen.Editor.route) {
            SleepEditorScreen(
                start = start.toInstant(),
                end = end.toInstant(),
                showStartDateTimePicker = { navController.navigate(Screen.StartDatePicker.route) },
                showEndDateTimePicker = { navController.navigate(Screen.EndDatePicker.route) },
                closeSleepEditor = { navController.popBackStack() }
            )
        }
        composable(Screen.StartDatePicker.route) {
            DatePicker(
                onDateConfirm = {
                    start = start.toLocalTime().atDate(it).atZone(ZoneId.systemDefault())
                    navController.navigate(Screen.StartTimePicker.route)
                },
                date = start.toLocalDate()
            )
        }
        composable(Screen.StartTimePicker.route) {
            TimePicker(
                onTimeConfirm = {
                    start = start.toLocalDate().atTime(it).atZone(ZoneId.systemDefault())
                    navController.popBackStack(route = Screen.Editor.route, inclusive = false)
                },
                time = start.toLocalTime()
            )
        }
        composable(Screen.EndDatePicker.route) {
            DatePicker(
                onDateConfirm = {
                    end = end.toLocalTime().atDate(it).atZone(ZoneId.systemDefault())
                    navController.navigate(Screen.EndTimePicker.route)
                },
                date = end.toLocalDate()
            )
        }
        composable(Screen.EndTimePicker.route) {
            TimePicker(
                onTimeConfirm = {
                    end = end.toLocalDate().atTime(it).atZone(ZoneId.systemDefault())
                    navController.popBackStack(route = Screen.Editor.route, inclusive = false)
                },
                time = end.toLocalTime()
            )
        }
    }
}

sealed class Screen(val route: String) {

    object Diary : Screen("diary")

    object Editor : Screen("editor")

    object Tracker : Screen("tracker")

    object StartDatePicker : Screen("startDatePicker")

    object StartTimePicker : Screen("startTimePicker")

    object EndDatePicker : Screen("endDatePicker")

    object EndTimePicker : Screen("endTimePicker")
}
