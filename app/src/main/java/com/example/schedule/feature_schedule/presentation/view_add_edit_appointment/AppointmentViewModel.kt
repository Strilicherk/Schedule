package com.example.schedule.feature_schedule.presentation.view_add_edit_appointment

import androidx.lifecycle.ViewModel
import com.example.schedule.feature_schedule.domain.use_case.appointment.AppointmentUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    val appointmentUseCases: AppointmentUseCases
): ViewModel() {

}