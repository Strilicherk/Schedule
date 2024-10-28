package com.example.schedule.feature_schedule.presentation.view_add_edit_appointment

import com.example.schedule.feature_schedule.domain.model.Appointment

sealed class AppointmentEvents {
    object OpenCreateAppointment: AppointmentEvents()
    object CloseAppointmentDetails: AppointmentEvents()

    data class OpenAppointmentDetails(val appointment: Appointment): AppointmentEvents()
    data class SaveAppointment(val appointment: Appointment): AppointmentEvents()
    data class DeleteAppointment(val appointment: Appointment): AppointmentEvents()

}