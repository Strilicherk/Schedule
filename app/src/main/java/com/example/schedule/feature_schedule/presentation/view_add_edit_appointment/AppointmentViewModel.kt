package com.example.schedule.feature_schedule.presentation.view_add_edit_appointment

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.schedule.feature_schedule.data.data_source.remote.AppointmentApi
import com.example.schedule.feature_schedule.domain.use_case.appointment.AppointmentUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val appointmentUseCases: AppointmentUseCases,
    private val api: AppointmentApi
): ViewModel() {
    val TAG = "Chamada API"

    init {
        val apiReturn = api.getAppointments()
        Log.d(TAG, "$apiReturn")
    }


    private var _state = mutableStateOf(AppointmentState())
    val state: State<AppointmentState> = _state

    fun onEvent(event: AppointmentEvents) {
        when(event) {
            AppointmentEvents.OpenCreateAppointment -> {
                _state.value.copy(isCreating = true)
            }

            AppointmentEvents.CloseCreateAppointment -> {
                _state.value.copy(isCreating = false)
            }

            AppointmentEvents.CloseAppointmentDetails -> TODO()

            is AppointmentEvents.DeleteAppointment -> TODO()
            is AppointmentEvents.OpenAppointmentDetails -> TODO()
            is AppointmentEvents.SaveAppointment -> TODO()
        }
    }
}