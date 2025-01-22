package com.example.schedule.feature_schedule.domain.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month

data class Day(
    val dayOfWeek: DayOfWeek,
    val date: LocalDate,
    val isCurrentMonth: Boolean
)

