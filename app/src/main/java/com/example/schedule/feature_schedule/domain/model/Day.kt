package com.example.schedule.feature_schedule.domain.model

import java.time.LocalDate

data class Day(
    val date: LocalDate,
    val isCurrentMonth: Boolean
)
