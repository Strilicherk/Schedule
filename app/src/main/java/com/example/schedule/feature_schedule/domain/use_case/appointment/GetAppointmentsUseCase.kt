package com.example.schedule.feature_schedule.domain.use_case.appointment

import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

class GetAppointmentsUseCase(
    private val repository: ScheduleRepository
) {

    /* Inserir para qual página isso vai depois? */
    operator fun invoke(): Flow<List<Appointment>> {
        return repository.getAppointments()
    }

}