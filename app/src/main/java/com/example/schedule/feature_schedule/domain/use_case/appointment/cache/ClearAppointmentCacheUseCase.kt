package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClearAppointmentCacheUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(): Resource<Boolean> {
        logger.info("Clearing appointment cache.")
        return repository.clearCache()
    }
}