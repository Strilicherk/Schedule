package com.example.schedule.feature_schedule.domain.use_case.schedule

import com.example.schedule.feature_schedule.domain.repository.ScheduleRepository

class NextMonthUseCase {
    operator fun invoke (month: Int): Int {
        val nextMonth = if (month < 12) month + 1 else month
        return nextMonth
    }
}