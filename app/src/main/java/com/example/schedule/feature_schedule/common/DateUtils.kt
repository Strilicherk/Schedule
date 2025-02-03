package com.example.schedule.feature_schedule.common

import com.example.schedule.feature_schedule.domain.model.Day
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth

fun generateMonthsData(): Map<Int, Map<Month, List<Day>>> {
    return (-1..1).associate { i ->
        val year = Year.now().plusYears(i.toLong()).value
        year to Month.entries.associateWith { month -> getMonthDays(year, month.value) }
    }
}

fun getMonthDays(year: Int, month: Int): List<Day> {
    val yearMonth = YearMonth.of(year, month)
    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()

    val daysBefore = (firstDayOfMonth.dayOfWeek.value % 7).let { offset ->
        (1..offset).map { i ->
            Day(
                dayOfWeek = firstDayOfMonth.minusDays(offset.toLong() - i + 1).dayOfWeek,
                date = firstDayOfMonth.minusDays(offset.toLong() - i + 1),
                isCurrentMonth = false
            )
        }
    }

    val currentMonthDays = (1..yearMonth.lengthOfMonth()).map { day ->
        Day(
            dayOfWeek = LocalDate.of(year, month, day).dayOfWeek,
            date = LocalDate.of(year, month, day),
            isCurrentMonth = true
        )
    }

    val totalDays = daysBefore.size + currentMonthDays.size
    val remainingDays = 42 - totalDays
    val daysAfter = (1..remainingDays).map { i ->
        Day(
            dayOfWeek = lastDayOfMonth.plusDays(i.toLong()).dayOfWeek,
            date = lastDayOfMonth.plusDays(i.toLong()),
            isCurrentMonth = false
        )
    }

    return daysBefore + currentMonthDays + daysAfter
}