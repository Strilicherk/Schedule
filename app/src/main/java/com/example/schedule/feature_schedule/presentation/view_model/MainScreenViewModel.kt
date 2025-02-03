package com.example.schedule.feature_schedule.presentation.view_model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.schedule.feature_schedule.domain.use_case.appointment.AppointmentUseCases
import com.example.schedule.feature_schedule.presentation.events.MainScreenEvents
import com.example.schedule.feature_schedule.presentation.events.CalendarEvents
import com.example.schedule.feature_schedule.presentation.states.MainScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val useCases: AppointmentUseCases
) : ViewModel() {
    private val _state = MutableStateFlow(
        MainScreenState(
            appointmentList = listOf()
        )
    )
    val state: StateFlow<MainScreenState> = _state.asStateFlow()

    fun onEvent(event: MainScreenEvents) {
//        when (event) {
//
//        }
    }
}
