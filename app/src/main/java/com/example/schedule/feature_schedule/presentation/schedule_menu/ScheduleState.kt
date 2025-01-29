package com.example.schedule.feature_schedule.presentation.schedule_menu

import com.example.schedule.feature_schedule.domain.model.Appointment
import java.time.LocalDate
import java.time.Month

data class ScheduleState(
    val appointmentList: List<Appointment> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val currentMonth: Month = LocalDate.now().month,
    val currentYear: Int = LocalDate.now().year,
    val currentIndex: Int = initialIndex()
) {
    companion object {
        fun initialIndex(): Int {
            val today = LocalDate.now()
            val currentMonthIndex = today.monthValue - 1
            return 12 + currentMonthIndex
        }
    }
}