package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDatesFromCacheByAppointmentUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(id: Int): Resource<List<Int>> {
        logger.info("Fetching dates from cache for appointment ID: $id")
        return repository.getDatesByAppointment(id)
    }
}