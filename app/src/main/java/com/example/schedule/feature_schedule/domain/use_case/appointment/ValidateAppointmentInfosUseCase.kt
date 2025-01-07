package com.example.schedule.feature_schedule.domain.use_case.appointment

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidateAppointmentInfosUseCase @Inject constructor(
    private val repository: AppointmentRepository,
) {
    suspend operator fun invoke(appointment: Appointment): Resource<Appointment> {
        val nextId = repository.getLastIdInCache().plus(1)
        var appointmentToCreate = appointment

        if (appointmentToCreate.id != nextId) appointmentToCreate = appointmentToCreate.copy(id = nextId)
        if (appointmentToCreate.title.isBlank()) return Resource.Error("Appointment title cannot be empty")

        if (appointmentToCreate.startDate.isAfter(appointmentToCreate.endDate)) return Resource.Error(
            "Appointment start date must be before end date"
        )

        if (appointmentToCreate.startTime.isAfter(appointmentToCreate.endTime)) return Resource.Error(
            "Appointment start time must be before end time"
        )

        return Resource.Success(appointmentToCreate)
    }
}