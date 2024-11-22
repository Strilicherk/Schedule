package com.example.schedule.feature_schedule.presentation.schedule_menu


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.model.RepeatOptionsEnum
import com.example.schedule.feature_schedule.domain.use_case.appointment.AppointmentUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val appointmentUseCases: AppointmentUseCases
) : ViewModel() {
    private var _state = mutableStateOf(
        ScheduleState(
            appointmentList = listOf()
        )
    )
    val state: State<ScheduleState> = _state


    fun onEvent(event: ScheduleEvents) {
        when (event) {
            is ScheduleEvents.NextMonth -> {
                TODO()
            }

            is ScheduleEvents.PreviousMonth -> {
                TODO()
            }

            is ScheduleEvents.SelectDay -> {
                TODO()
            }
        }
    }

}