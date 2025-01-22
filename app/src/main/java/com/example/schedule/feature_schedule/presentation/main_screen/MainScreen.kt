package com.example.schedule.feature_schedule.presentation.main_screen

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schedule.feature_schedule.presentation.schedule_menu.ScheduleViewModel
import com.example.schedule.feature_schedule.presentation.schedule_menu.components.AppointmentListComponent
import com.example.schedule.feature_schedule.presentation.schedule_menu.components.ScheduleComponent
import com.example.schedule.feature_schedule.presentation.view_add_edit_appointment.AppointmentEvents
import com.example.schedule.feature_schedule.presentation.view_add_edit_appointment.AppointmentViewModel
import com.example.schedule.ui.theme.CustomCyan

@Composable
fun MainScreen(
    appointmentViewModel: AppointmentViewModel = hiltViewModel(),
    scheduleViewModel: ScheduleViewModel = hiltViewModel(),
) {
    val appointmentState = appointmentViewModel.state.value
    val scheduleState = scheduleViewModel.state.value

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    appointmentViewModel.onEvent(AppointmentEvents.OpenCreateAppointment)
                    if (appointmentState.isCreating) {

                    }
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
            Column (
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                MainScreenHeader()
                ScheduleComponent()
                AppointmentListComponent()
            }
        }
    )
}