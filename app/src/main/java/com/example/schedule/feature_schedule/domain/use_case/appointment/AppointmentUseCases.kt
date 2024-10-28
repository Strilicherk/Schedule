package com.example.schedule.feature_schedule.domain.use_case.appointment

data class AppointmentUseCases(
    val getAppointments: GetAppointmentsUseCase,
    val insertOrUpdateAppointment: InsertOrUpdateAppointmentUseCase,
    val deleteAppointment: DeleteAppointmentUseCase
)
