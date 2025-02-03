package com.example.schedule.feature_schedule.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.schedule.feature_schedule.presentation.components.main_screen.MainScreenHeader
import com.example.schedule.feature_schedule.presentation.view_model.MainScreenViewModel
import com.example.schedule.feature_schedule.presentation.components.main_screen.AppointmentListComponent
import com.example.schedule.feature_schedule.presentation.components.main_screen.CalendarComponent
import com.example.schedule.feature_schedule.presentation.view_model.AppointmentViewModel
import com.example.schedule.ui.theme.CustomCyan

@Composable
fun MainScreen(
    navController: NavController
) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("upsertAppointment")
                },
                shape = CircleShape,
                containerColor = CustomCyan,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add Appointment",
                    tint = White,
                    modifier = Modifier
                        .size(35.dp)
                )
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                MainScreenHeader()
                CalendarComponent(screen = "Main")
                AppointmentListComponent()
            }
        }
    )
}