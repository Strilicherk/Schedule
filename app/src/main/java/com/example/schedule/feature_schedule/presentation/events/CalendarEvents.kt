package com.example.schedule.feature_schedule.presentation.events

import androidx.compose.ui.geometry.Offset
import com.example.schedule.feature_schedule.domain.model.Day
import java.time.LocalDate
import java.time.Month

sealed class CalendarEvents {
    data class SelectDay(val date: LocalDate): CalendarEvents()
    data class ChangeVisibleMonth(val dragAmount: Offset?, val day: Day?, val totalItems: Int, val screen: String): CalendarEvents()
}