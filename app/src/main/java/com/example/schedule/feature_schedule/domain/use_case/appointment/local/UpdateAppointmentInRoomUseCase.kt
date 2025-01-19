package com.example.schedule.feature_schedule.domain.use_case.appointment.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.ValidateAppointmentInfosUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateAppointmentInRoomUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase,
    private val logger: Logger
) {
    suspend operator fun invoke(appointment: Appointment): Resource<Boolean> {
        val validatedAppointment = validateAppointmentInfosUseCase.invoke(appointment)
        if (validatedAppointment is Resource.Success) {
            val res = repository.updateAppointmentInRoom(appointment)
            if (res is Resource.Error) logger.error(res.message) else logger.info("Successfully updated appointment in room.")
            return res
        } else {
            logger.error(validatedAppointment.message)
            return Resource.Error("${validatedAppointment.message}")
        }
    }
}