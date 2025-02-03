package com.example.schedule.feature_schedule.presentation.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schedule.feature_schedule.domain.use_case.appointment.AppointmentUseCases
import com.example.schedule.feature_schedule.presentation.events.CalendarEvents
import com.example.schedule.feature_schedule.presentation.states.CalendarState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Month
import java.time.Year
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val useCases: AppointmentUseCases
) : ViewModel() {

    companion object {
        private const val TAG = "CalendarDebug"
    }

    private val _state = MutableStateFlow(CalendarState())
    val state: StateFlow<CalendarState> = _state.asStateFlow()
    private var updateJob: Job? = null

    fun onEvent(event: CalendarEvents) {
        when (event) {
            is CalendarEvents.SelectDay -> {
                Log.d(TAG, "Evento SelectDay: ${event.date}")
                _state.value = _state.value.copy(selectedDate = event.date)
            }

            is CalendarEvents.ChangeVisibleMonth -> {

                updateJob?.cancel()
                updateJob = viewModelScope.launch {
                    delay(90)
                    Log.d(
                        TAG,
                        "Evento ChangeVisibleMonth: screen = ${event.screen}, dragAmount = ${event.dragAmount}, day = ${event.day?.date}"
                    )

                    viewModelScope.launch {
                        val currentIndex = if (event.screen == "Main") {
                            _state.value.currentMainScreenMonthIndex
                        } else {
                            _state.value.currentAppointmentScreenMonthIndex
                        }

                        Log.d(TAG, "Índice atual antes do UseCase: $currentIndex")

                        val newIndex = useCases.changeVisibleMonthUseCase.invoke(
                            currentIndex = currentIndex,
                            dragAmount = event.dragAmount,
                            day = event.day,
                            totalItems = event.totalItems
                        )

                        val year = Year.now().value + (newIndex / 12) - 1
                        val month = Month.entries[newIndex % 12]

                        Log.d(TAG, "Novo índice: $newIndex, Ano: $year, Mês: $month")

                        if (event.screen == "Main") {
                            Log.d(TAG, "Screen: ${event.screen}")
                            _state.value = _state.value.copy(
                                currentMainScreenMonthIndex = newIndex,
                                currentMainScreenMonth = month,
                                currentMainScreenYear = year
                            )
                        } else {
                            Log.d(TAG, "Screen: ${event.screen}")
                            _state.value = _state.value.copy(
                                currentAppointmentScreenMonthIndex = newIndex,
                                currentAppointmentScreenMonth = month,
                                currentAppointmentScreenYear = year
                            )
                        }

                        Log.d(
                            TAG,
                            "State atualizado: currentIndex = ${state.value.currentMainScreenMonthIndex} " +
                                    "currentMonth = ${state.value.currentMainScreenMonth}, " +
                                    "currentYear = ${state.value.currentMainScreenYear}"
                        )
                    }
                }
            }
        }
    }
}