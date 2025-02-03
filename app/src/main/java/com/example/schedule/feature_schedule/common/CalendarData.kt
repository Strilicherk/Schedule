package com.example.schedule.feature_schedule.common

import com.example.schedule.feature_schedule.domain.model.Day
import java.time.Month

object CalendarData {
    val monthsData: Map<Int, Map<Month, List<Day>>> by lazy { generateMonthsData() }
}