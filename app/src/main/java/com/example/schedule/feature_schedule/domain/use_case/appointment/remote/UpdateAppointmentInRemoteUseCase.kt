package com.example.schedule.feature_schedule.domain.use_case.appointment.remote

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import org.slf4j.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateAppointmentInRemoteUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val logger: Logger
){
    suspend operator fun invoke(appointment: Appointment): Resource<Boolean> {
        logger.info("Updating appointment in remote")
        val appointmentToUpdate = appointment.copy(isSynced = true, hasBeenSynced = true)
        val response = repository.updateRemoteAppointment(appointmentToUpdate)
        if (response is Resource.Success) logger.info("Appointment successfully updated in remote")
        else logger.error("Failed to update appointment in remote")
        return response
    }
}