package com.example.schedule.feature_schedule.presentation.schedule_menu

sealed class ScheduleEvents {
    object NextMonth: ScheduleEvents()
    object PreviousMonth: ScheduleEvents()
    object SelectDay: ScheduleEvents()
}