package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.ValidateAppointmentInfosUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.AddAppointmentToRoomUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddAppointmentToCacheUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase,
    private val addAppointmentToRoomUseCase: AddAppointmentToRoomUseCase,
    private val logger: Logger
) {
    suspend operator fun invoke(appointment: Appointment): Resource<Boolean> {
        logger.info("Validating appointment information.")
        val result = validateAppointmentInfosUseCase.invoke(appointment)
        if (result is Resource.Error) {
            logger.error(result.message)
            return Resource.Error(result.message ?: "Validation failed")
        }
        val appointmentToCreate = result.data!!

        logger.info("Creating appointment in Room.")
        val createAppointmentInRoom = withContext(Dispatchers.IO) {
            addAppointmentToRoomUseCase.invoke(appointmentToCreate)
        }

        return if (createAppointmentInRoom is Resource.Success) {
            logger.info("Adding appointment to cache.")
            repository.addAppointmentToCache(appointmentToCreate)
            val dateRange = generateSequence(appointment.startDate) { current ->
                if (current.isBefore(appointment.endDate) || current.isEqual(appointment.endDate))
                    current.plusDays(1) else null
            }
            dateRange.forEach { date ->
                val key = repository.generateDateKey(date.dayOfMonth, date.monthValue, date.year)
                repository.addAppointmentToByDateCache(key, appointment.id)
                repository.addDateToByAppointmentCache(appointment.id, key)
            }
            logger.info("Appointment added successfully to cache.")
            Resource.Success(true)
        } else {
            logger.error(createAppointmentInRoom.message)
            Resource.Error("Failed to create appointment in Room.")
        }
    }
}
