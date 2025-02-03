package com.example.schedule.feature_schedule.presentation.states

import androidx.compose.ui.graphics.Color
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.ui.theme.CustomLightBlue
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

    val appointmentTitle: String = "",
    val appointmentColor: Color = CustomLightBlue,

    val appointmentStartDate: LocalDateTime = LocalDateTime.now(),
    val appointmentEndDate: LocalDateTime = LocalDateTime.now(),
    val appointmentNotes: String = "",

//    val appointmentRepeat: Boolean = false,
//    val appointmentRepeatOption: RepeatOptionsEnum = RepeatOptionsEnum.NEVER,
    val appointmentIsCreating: Boolean = false,
)
