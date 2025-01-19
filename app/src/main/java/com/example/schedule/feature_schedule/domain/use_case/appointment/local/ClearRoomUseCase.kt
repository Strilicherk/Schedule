package com.example.schedule.feature_schedule.domain.use_case.appointment.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import org.slf4j.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClearRoomUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val logger: Logger
){
    suspend operator fun invoke(): Resource<Boolean> {
        logger.info("Clearing local database.")
        val res = repository.clearAppointmentTable()
        if (res is Resource.Error) logger.error(res.message) else logger.info("Successfully cleared local database.")
        return res
    }
}