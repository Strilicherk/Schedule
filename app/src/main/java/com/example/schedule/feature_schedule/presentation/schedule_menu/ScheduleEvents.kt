package com.example.schedule.feature_schedule.presentation.schedule_menu

import java.time.LocalDate
import java.time.Month

sealed class ScheduleEvents {
    data class SelectDay(val date: LocalDate): ScheduleEvents()
    data class UpdateViewingDate(val year: Int, val month: Month): ScheduleEvents()
}