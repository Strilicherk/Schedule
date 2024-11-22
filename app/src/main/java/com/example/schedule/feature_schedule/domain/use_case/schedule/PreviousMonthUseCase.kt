package com.example.schedule.feature_schedule.domain.use_case.schedule

class PreviousMonthUseCase {
    operator fun invoke (month: Int): Int {
        val previousMonth = if (month > 1) month - 1 else month
        return previousMonth
    }
}