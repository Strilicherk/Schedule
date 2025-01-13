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
    suspend operator fun invoke(appointment: Appointment, isUpdate: Boolean): Resource<Appointment> {
        var appointmentToUpsert = appointment

        if (appointmentToUpsert.title.isBlank()) return Resource.Error("Appointment title cannot be empty")
        if (appointmentToUpsert.startDate.isAfter(appointmentToUpsert.endDate)) return Resource.Error(
            "Appointment start date must be before end date"
        )
        if (appointmentToUpsert.startTime.isAfter(appointmentToUpsert.endTime)) return Resource.Error(
            "Appointment start time must be before end time"
        )


        if (!isUpdate) {
            val nextId = repository.getLastIdInCache().plus(1)
            if (appointmentToUpsert.id != nextId) appointmentToUpsert = appointmentToUpsert.copy(id = nextId)
            appointmentToUpsert = appointmentToUpsert.copy(isSynced = false, hasBeenSynced = false)
        } else {
            appointmentToUpsert = appointmentToUpsert.copy(isSynced = false)
        }

        return Resource.Success(appointmentToUpsert)
    }
}