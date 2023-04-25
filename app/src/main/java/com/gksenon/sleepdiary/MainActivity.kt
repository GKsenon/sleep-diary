package com.gksenon.sleepdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gksenon.sleepdiary.view.SleepDiaryScreen
import com.gksenon.sleepdiary.view.SleepEditorScreen
import com.gksenon.sleepdiary.view.SleepTrackerScreen
import dagger.hilt.android.AndroidEntryPoint

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
fun MainScreen(navController: NavHostController = rememberNavController()) {
    NavHost(navController, startDestination = "sleep_diary") {
        composable("sleep_diary") {
            SleepDiaryScreen(
                onNavigateToSleepEditor = { navController.navigate("sleep_editor") },
                onNavigateToSleepTracking = { navController.navigate("sleep_tracker") })
        }
        composable("sleep_editor") {
            SleepEditorScreen(onSaveSleep = { navController.popBackStack() })
        }
        composable("sleep_tracker") {
            SleepTrackerScreen()
        }
    }
}