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
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.presentation.schedule_menu.ScheduleViewModel
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun AppointmentListComponent(viewModel: ScheduleViewModel = hiltViewModel()) {
    val appointmentCache = listOf(
        Appointment(
            id = 1,
            title = "Reunião",
            notes = "Discussão sobre projetos",
            color = 0xFF0000,
            startDate = LocalDate.of(2025, 1, 21),
            endDate = LocalDate.of(2025, 1, 21),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 0)
        ),
        Appointment(
            id = 2,
            title = "Consulta médica",
            notes = "Consulta geral",
            color = 0x00FF00,
            startDate = LocalDate.of(2025, 1, 22),
            endDate = LocalDate.of(2025, 1, 22),
            startTime = LocalTime.of(14, 0),
            endTime = LocalTime.of(15, 0)
        ),
        Appointment(
            id = 3,
            title = "Jantar",
            notes = "Jantar com amigos",
            color = 0x0000FF,
            startDate = LocalDate.of(2025, 1, 23),
            endDate = LocalDate.of(2025, 1, 23),
            startTime = LocalTime.of(19, 30),
            endTime = LocalTime.of(22, 0)
        ),
        Appointment(
            id = 4,
            title = "Aniversário",
            notes = "Festa de aniversário",
            color = 0xFFFF00,
            startDate = LocalDate.of(2025, 1, 24),
            endDate = LocalDate.of(2025, 1, 24),
            startTime = LocalTime.of(18, 0),
            endTime = LocalTime.of(22, 0)
        ),
        Appointment(
            id = 4,
            title = "Aniversário",
            notes = "Festa de aniversário",
            color = 0xFFFF00,
            startDate = LocalDate.of(2025, 1, 24),
            endDate = LocalDate.of(2025, 1, 24),
            startTime = LocalTime.of(18, 0),
            endTime = LocalTime.of(22, 0)
        ),
        Appointment(
            id = 4,
            title = "Aniversário",
            notes = "Festa de aniversário",
            color = 0xFFFF00,
            startDate = LocalDate.of(2025, 1, 24),
            endDate = LocalDate.of(2025, 1, 24),
            startTime = LocalTime.of(18, 0),
            endTime = LocalTime.of(22, 0)
        ),
        Appointment(
            id = 4,
            title = "Aniversário",
            notes = "Festa de aniversário",
            color = 0xFFFF00,
            startDate = LocalDate.of(2025, 1, 24),
            endDate = LocalDate.of(2025, 1, 24),
            startTime = LocalTime.of(18, 0),
            endTime = LocalTime.of(22, 0)
        ),
        Appointment(
            id = 4,
            title = "Aniversário",
            notes = "Festa de aniversário",
            color = 0xFFFF00,
            startDate = LocalDate.of(2025, 1, 24),
            endDate = LocalDate.of(2025, 1, 24),
            startTime = LocalTime.of(18, 0),
            endTime = LocalTime.of(22, 0)
        ),
        Appointment(
            id = 4,
            title = "Aniversário",
            notes = "Festa de aniversário",
            color = 0xFFFF00,
            startDate = LocalDate.of(2025, 1, 24),
            endDate = LocalDate.of(2025, 1, 24),
            startTime = LocalTime.of(18, 0),
            endTime = LocalTime.of(22, 0)
        ),
        Appointment(
            id = 4,
            title = "Aniversário",
            notes = "Festa de aniversário",
            color = 0xFFFF00,
            startDate = LocalDate.of(2025, 1, 24),
            endDate = LocalDate.of(2025, 1, 24),
            startTime = LocalTime.of(18, 0),
            endTime = LocalTime.of(22, 0)
        ),
        Appointment(
            id = 4,
            title = "Aniversário",
            notes = "Festa de aniversário",
            color = 0xFFFF00,
            startDate = LocalDate.of(2025, 1, 24),
            endDate = LocalDate.of(2025, 1, 24),
            startTime = LocalTime.of(18, 0),
            endTime = LocalTime.of(22, 0)
        ),
    )

    LazyColumn(
        modifier = Modifier
            .padding(top = 30.dp)
            .fillMaxHeight()
    ) {
        items(appointmentCache) { appointment ->
            AppointmentRow(appointment)
        }
    }
}

@Composable
fun AppointmentRow(appointment: Appointment) {
    val noteColors = mapOf(
        0xFF0000 to Color.Red,
        0x00FF00 to Color.Green,
        0x0000FF to Color.Blue,
        0xFFFF00 to Color.Yellow
    )

    Row(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, bottom = 7.dp)
            .fillMaxWidth()
            .height(70.dp)
            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(top = 17.dp)
                .width(25.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = noteColors.getOrDefault(appointment.color, Color.Gray),
                        shape = CircleShape
                    )
                    .align(Alignment.End)
            )
        }
        Column(
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