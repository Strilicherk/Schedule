package com.example.schedule.feature_schedule.domain.use_case.appointment

import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.ScheduleRepository

class DeleteAppointmentUseCase(
    private val repository: ScheduleRepository
) {
    suspend operator fun invoke(appointment: Appointment) {
        return repository.deleteAppointment(appointment)
    }
}