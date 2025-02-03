package com.example.schedule.feature_schedule.presentation.events

import androidx.compose.ui.graphics.Color
import com.example.schedule.feature_schedule.domain.model.Appointment

sealed class AppointmentEvents {
    object OpenCreateAppointment: AppointmentEvents()
    object CloseCreateAppointment: AppointmentEvents()
    object CloseAppointmentDetails: AppointmentEvents()

    data class OpenAppointmentDetails(val appointment: Appointment): AppointmentEvents()
    data class SaveAppointment(val appointment: Appointment): AppointmentEvents()
    data class DeleteAppointment(val appointment: Appointment): AppointmentEvents()
    data class SelectAppointmentColor(val color: Color): AppointmentEvents()

}