package com.example.schedule.feature_schedule.domain.use_case.appointment.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import org.slf4j.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteAppointmentFromRoomUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(id: Int): Resource<Boolean> {
        logger.info("Trying to delete appointment in Room")
        val res = repository.deleteAppointmentFromRoom(id)
        return if (res is Resource.Success) {
            logger.info("Appointment deleted")
            Resource.Success(true)
        } else {
            logger.error("Unable to delete appointment: ${res.message}")
            Resource.Error("${res.message}")
        }
    }
}