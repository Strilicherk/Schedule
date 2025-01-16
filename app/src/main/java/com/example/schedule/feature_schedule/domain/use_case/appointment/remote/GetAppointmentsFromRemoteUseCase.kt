package com.example.schedule.feature_schedule.domain.use_case.appointment.remote

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import org.slf4j.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAppointmentsFromRemoteUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(): Resource<List<Appointment>> {
        logger.info("Fetching appointments from remote")
        val response = repository.getRemoteAppointments()
        if (response is Resource.Success) logger.info("Appointment list success fetched from remote")
        else logger.error("Failed to fetch appointment list from remote")
        return response
    }
}

