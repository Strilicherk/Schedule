package com.example.schedule.feature_schedule.domain.use_case.appointment.remote

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import org.slf4j.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddAppointmentToRemoteUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(appointment: Appointment): Resource<Boolean> {
        logger.info("Adding appointment to remote")
        val response = repository.addAppointmentToRemote(appointment)
        if (response is Resource.Success) logger.info("Appointment success added to remote")
        else logger.error("Failed to add appointment in remote")
        return response
    }
}