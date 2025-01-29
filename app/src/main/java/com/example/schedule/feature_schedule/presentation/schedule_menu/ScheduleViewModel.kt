package com.example.schedule.feature_schedule.presentation.schedule_menu


import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.model.Day
import com.example.schedule.feature_schedule.domain.use_case.appointment.AppointmentUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.Year
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val appointmentUseCases: AppointmentUseCases
) : ViewModel() {

    val monthsData: Map<Int, Map<Month, List<Day>>> = (-1..1).associate { i ->
        val year = Year.now().plusYears(i.toLong()).value
        year to Month.entries.associateWith { month -> getMonthDays(year, month.value) }
    }

    private val _state = MutableStateFlow(
        ScheduleState(
            appointmentList = listOf()
        )
    )
    val state: StateFlow<ScheduleState> = _state.asStateFlow()

    fun onEvent(event: ScheduleEvents) {
        when (event) {
            is ScheduleEvents.SelectDay -> {
                _state.value = _state.value.copy(
                    selectedDate = event.date
                )
            }

            is ScheduleEvents.UpdateViewingDate -> {
                _state.value = _state.value.copy(
                    currentYear = event.year,
                    currentMonth = event.month,
                    currentIndex = event.currentIndex
                )
                Log.d("viewModel", "${_state.value}")
                Log.d("viewModel", "${event.currentIndex}")
            }
        }
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
