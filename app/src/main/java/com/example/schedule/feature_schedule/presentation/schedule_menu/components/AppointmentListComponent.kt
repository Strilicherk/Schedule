package com.example.schedule.feature_schedule.presentation.schedule_menu.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.presentation.schedule_menu.ScheduleViewModel

@Composable
fun AppointmentListComponent(viewModel: ScheduleViewModel = hiltViewModel()) {
    val scheduleState = viewModel.state.value

    LazyColumn (
        modifier = Modifier
            .padding(top = 30.dp)
            .fillMaxHeight()
    ) {
        items(scheduleState.appointmentList) { appointment ->
            AppointmentRow(appointment)
        }
    }
}

@Composable
fun AppointmentRow(appointment: Appointment) {
    Row (
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, bottom = 20.dp)
            .fillMaxWidth()
            .height(70.dp)
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(20.dp))

    ){
        Column (
            modifier = Modifier
                .padding(top = 17.dp)
                .width(25.dp)

        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Appointment.noteColors[appointment.color], CircleShape)
                    .align(Alignment.End)
            )
        }
        Column (
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp, start = 10.dp)
                .fillMaxHeight()
                .fillMaxWidth(0.95f)
        ) {
            Text(
                text = appointment.title,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                fontWeight = MaterialTheme.typography.titleMedium.fontWeight
            )
            Text(
                text = appointment.notes,
                fontSize = MaterialTheme.typography.displaySmall.fontSize,
                fontWeight = MaterialTheme.typography.displaySmall.fontWeight
            )
        }


    }
}