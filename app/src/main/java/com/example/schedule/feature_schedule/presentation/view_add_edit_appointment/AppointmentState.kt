package com.example.schedule.feature_schedule.presentation.view_add_edit_appointment

import androidx.compose.ui.graphics.Color
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.model.RepeatOptionsEnum
import java.time.LocalDateTime

data class AppointmentState(
    val appointmentList: List<Appointment> = emptyList(),
    val appointment: Appointment = Appointment(
        title = "",
        notes = "",
        color = 1,
//        repeat = false,
//        repeatOption = RepeatOptionsEnum.NEVER,
        endDate = "01-11-2024 07:00:00 AM",
        startDate = "01-11-2024 10:00:00 AM"
    ),

    val title: String = "",
    val notes: String = "",
    val color: Color = Color.Red,
    val startDate: LocalDateTime = LocalDateTime.now(),
    val endDate: LocalDateTime = LocalDateTime.now(),
    val repeat: Boolean = false,
    val repeatOption: RepeatOptionsEnum = RepeatOptionsEnum.NEVER,
    val isCreating: Boolean = false
)
