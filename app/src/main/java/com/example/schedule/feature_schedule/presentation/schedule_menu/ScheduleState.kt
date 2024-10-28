package com.example.schedule.feature_schedule.presentation.schedule_menu

import androidx.compose.ui.graphics.Color
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.model.RepeatOptionsEnum
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.MonthDay
import kotlin.time.Duration.Companion.days

data class ScheduleState(
    val appointmentList: List<Appointment> = emptyList(),
    val day: Int = LocalDate.now().dayOfMonth,
    val month: Int = LocalDate.now().monthValue,
    val year: Int = LocalDate.now().year
)
