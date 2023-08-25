package com.gksenon.sleepdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.gksenon.sleepdiary.theme.SleepDiaryTheme
import com.gksenon.sleepdiary.view.DiaryScreen
import com.gksenon.sleepdiary.view.TrackerScreen
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Date

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
    SwipeDismissableNavHost(navController = navController, startDestination = "diary") {
        composable("diary") { DiaryScreen(onTrackButtonClicked = { navController.navigate("tracker") }) }
        composable("tracker") { TrackerScreen() }
    }
}
