package com.example.schedule.feature_schedule.presentation.states

import com.example.schedule.feature_schedule.common.CalendarData
import com.example.schedule.feature_schedule.domain.model.Day
import java.time.LocalDate
import java.time.Month

data class CalendarState(
    val selectedDate: LocalDate = LocalDate.now(),
    val monthsData: Map<Int, Map<Month, List<Day>>> = CalendarData.monthsData,

    val currentMainScreenMonthIndex: Int = initialIndex(),
    val currentMainScreenMonth: Month = LocalDate.now().month,
    val currentMainScreenYear: Int = LocalDate.now().year,

    val currentAppointmentScreenMonthIndex: Int = initialIndex(),
    val currentAppointmentScreenMonth: Month = LocalDate.now().month,
    val currentAppointmentScreenYear: Int = LocalDate.now().year
) {
    companion object {
        fun initialIndex(): Int {
            val today = LocalDate.now()
            val currentMonthIndex = today.monthValue - 1
            return 12 + currentMonthIndex
        }
    }
}