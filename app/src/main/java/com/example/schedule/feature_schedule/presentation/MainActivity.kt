package com.example.schedule.feature_schedule.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.schedule.feature_schedule.presentation.screens.MainScreen
import com.example.schedule.feature_schedule.presentation.screens.CreateOrUpdateAppointmentScreen
import com.example.schedule.ui.theme.CalendarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            @Composable
            fun AppNavigation(){
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable(route = "home") { MainScreen(navController = navController) }
                    composable(
                        route = "upsertAppointment",
                        enterTransition = { slideInVertically(initialOffsetY = { fullHeight -> fullHeight }, animationSpec = tween(300)) },
                        exitTransition = { slideOutVertically(targetOffsetY = { fullHeight -> -fullHeight }, animationSpec = tween(300)) }
                    ) { CreateOrUpdateAppointmentScreen(navController) }
                }
            }

            CalendarTheme {
                AppNavigation()
            }
        }
    }
}

