package com.example.schedule.feature_schedule.presentation.schedule_menu

import com.example.schedule.feature_schedule.domain.model.Appointment
import java.time.LocalDate

data class ScheduleState(
    val appointmentList: List<Appointment> = emptyList(),
    val day: Int = LocalDate.now().dayOfMonth,
    val month: Int = LocalDate.now().monthValue,
    val year: Int = LocalDate.now().year
)
