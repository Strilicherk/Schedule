package com.example.schedule.feature_schedule.presentation.schedule_menu

import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.model.Day
import java.time.LocalDate
import java.time.Month
import java.time.Year

data class ScheduleState(
    val appointmentList: List<Appointment> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val currentMonth: Month = LocalDate.now().month,
    val currentYear: Int = LocalDate.now().year
)
