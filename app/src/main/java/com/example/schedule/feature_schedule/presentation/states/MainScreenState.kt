package com.example.schedule.feature_schedule.presentation.states

import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.presentation.states.CalendarState.Companion.initialIndex
import java.time.LocalDate
import java.time.Month

data class MainScreenState(
    val appointmentList: List<Appointment> = emptyList(),
    val currentIndex: Int = initialIndex(),
    val currentMonth: Month = LocalDate.now().month,
    val currentYear: Int = LocalDate.now().year
)
