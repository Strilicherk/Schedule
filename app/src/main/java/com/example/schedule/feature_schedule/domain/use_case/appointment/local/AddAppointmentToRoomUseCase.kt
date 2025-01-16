package com.example.schedule.feature_schedule.domain.use_case.appointment.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.ValidateAppointmentInfosUseCase
import org.slf4j.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddAppointmentToRoomUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase,
    private val logger: Logger
) {
    suspend operator fun invoke(appointment: Appointment): Resource<Boolean> {
        logger.info("Validating appointment information.")
        val result = validateAppointmentInfosUseCase.invoke(appointment)
        if (result is Resource.Error) {
            logger.error("Validation failed: ${result.message}")
            return Resource.Error(result.message ?: "Validation failed")
        }
        val validatedAppointment = result.data!!

        logger.info("Adding appointment to Room.")
        return repository.addAppointmentToRoom(validatedAppointment)
    }
}

