package com.example.schedule.feature_schedule.domain.use_case.appointment

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetLastIdInCacheUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidateAppointmentInfosUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val getLastIdInCacheUseCase: GetLastIdInCacheUseCase
) {
    suspend operator fun invoke(appointment: Appointment): Resource<Appointment> {
        val lastId = getLastIdInCacheUseCase.invoke()
        val appointmentToCreate =
            if (appointment.id != lastId) appointment.copy(id = lastId) else appointment

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