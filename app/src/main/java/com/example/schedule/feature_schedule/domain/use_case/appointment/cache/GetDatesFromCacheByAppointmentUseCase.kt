package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import org.slf4j.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDatesFromCacheByAppointmentUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(id: Int): Resource<List<Int>> {
        logger.info("Fetching dates from cache for appointment ID: $id")
        val res = repository.getDatesByAppointment(id)
        if (res is Resource.Error) logger.error(res.message) else logger.info("Successfully fetched dates from cache for appointment ID: $id")
        return res
    }
}