package com.example.schedule.feature_schedule.presentation.view_model

import androidx.lifecycle.ViewModel
import com.example.schedule.feature_schedule.data.data_source.remote.AppointmentApi
import com.example.schedule.feature_schedule.domain.use_case.appointment.AppointmentUseCases
import com.example.schedule.feature_schedule.presentation.events.AppointmentEvents
import com.example.schedule.feature_schedule.presentation.states.AppointmentState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val useCases: AppointmentUseCases,
    private val api: AppointmentApi
): ViewModel() {
    val TAG = "Chamada API"

    private val _state = MutableStateFlow(AppointmentState(appointmentList = listOf()))
    val state: StateFlow<AppointmentState> = _state.asStateFlow()

    fun onEvent(event: AppointmentEvents) {
        when(event) {
            AppointmentEvents.OpenCreateAppointment -> {
               _state.value = _state.value.copy(appointmentIsCreating = true)
            }

            AppointmentEvents.CloseCreateAppointment -> {
                _state.value = _state.value.copy(appointmentIsCreating = false)
            }

            AppointmentEvents.CloseAppointmentDetails -> TODO()

            is AppointmentEvents.DeleteAppointment -> TODO()
            is AppointmentEvents.OpenAppointmentDetails -> TODO()
            is AppointmentEvents.SaveAppointment -> TODO()
            is AppointmentEvents.SelectAppointmentColor -> {
                _state.value = _state.value.copy(appointmentColor = event.color)
            }
        }
    }
}