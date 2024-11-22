package com.example.schedule.feature_schedule.domain.use_case.schedule

class NextMonthUseCase {
    operator fun invoke (month: Int): Int {
        val nextMonth = if (month < 12) month + 1 else month
        return nextMonth
    }
}