package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAppointmentFromCacheByIdUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(id: Int): Resource<Appointment> {
        logger.info("Fetching appointment from cache by ID: $id")
        val res = repository.getAppointmentById(id)
        if (res is Resource.Error) logger.error(res.message) else logger.info("Successfully fetched appointment from cache by ID: $id")
        return res
    }
}