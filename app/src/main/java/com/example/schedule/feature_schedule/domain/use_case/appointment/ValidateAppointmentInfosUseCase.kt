package com.example.schedule.feature_schedule.domain.use_case.appointment

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import org.slf4j.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidateAppointmentInfosUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(appointment: Appointment): Resource<Appointment> {
        var appointmentToUpsert = appointment
        logger.info("Validating appointment with title: ${appointmentToUpsert.title}")

        require(appointmentToUpsert.title.isBlank()) {
            val errorMessage = "Appointment title cannot be empty"
            logger.error(errorMessage)
            return Resource.Error("Appointment title cannot be empty")
        }
        require(appointmentToUpsert.startDate.isAfter(appointmentToUpsert.endDate)) {
            val errorMessage = "Appointment start date must be before end date"
            logger.error(errorMessage)
            return Resource.Error("Appointment start date must be before end date")
        }
        require(appointmentToUpsert.startTime.isAfter(appointmentToUpsert.endTime)) {
            val errorMessage = "Appointment start time must be before end time"
            logger.error(errorMessage)
            return Resource.Error("Appointment start time must be before end time")
        }

        logger.info("Fetching last ID from repository for new appointment")
        val result = repository.getLastIdInCache()
        if (result is Resource.Success) {
            val nextId = result.data!!.plus(1)
            if (appointmentToUpsert.id != nextId) {
                appointmentToUpsert = appointmentToUpsert.copy(id = nextId)
            }
        } else {
            logger.error("Error fetching last ID: ${result.message}")
            return Resource.Error("${result.message}")
        }

        return Resource.Success(appointmentToUpsert)
    }
}