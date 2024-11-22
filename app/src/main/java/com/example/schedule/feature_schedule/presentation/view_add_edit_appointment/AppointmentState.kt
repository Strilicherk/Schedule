package com.example.schedule.feature_schedule.presentation.view_add_edit_appointment

import androidx.compose.ui.graphics.Color
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.model.RepeatOptionsEnum
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class AppointmentState(
    val appointmentList: List<Appointment> = emptyList(),
    val appointment: Appointment = Appointment(
        id = 1,
        title = "",
        notes = "",
        color = 1,
        startDate = LocalDate.now(),
        endDate = LocalDate.now(),
        startTime = LocalTime.now(),
        endTime = LocalTime.now(),
        lastModified = LocalDateTime.now(),
        isSynced = false
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
