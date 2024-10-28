package com.example.schedule.feature_schedule.presentation.schedule_menu.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schedule.feature_schedule.presentation.schedule_menu.ScheduleViewModel

@Composable
fun AppointmentListComponent(
    viewModel: ScheduleViewModel = hiltViewModel(),
) {
    val appointmentState = viewModel.state.value
    LazyColumn {
        items(appointmentState.appointmentList) { appointment ->
            
        }
    }
}