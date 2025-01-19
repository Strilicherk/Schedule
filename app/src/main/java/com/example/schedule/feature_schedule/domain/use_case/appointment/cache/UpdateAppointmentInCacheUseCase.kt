package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.ValidateAppointmentInfosUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpdateAppointmentInRoomUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateAppointmentInCacheUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val addAppointmentToCacheUseCase: AddAppointmentToCacheUseCase,
    private val updateAppointmentInRoomUseCase: UpdateAppointmentInRoomUseCase,
    private val getDatesFromCacheByAppointmentUseCase: GetDatesFromCacheByAppointmentUseCase,
    private val getAppointmentFromCacheByIdUseCase: GetAppointmentFromCacheByIdUseCase,
    private val validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase,
    private val logger: Logger
) {
    suspend operator fun invoke(appointment: Appointment): Resource<Boolean> {
        logger.info("Validating appointment for update.")
        val validatedAppointment = validateAppointmentInfosUseCase.invoke(appointment).data
            ?: return Resource.Error("Validation failed")
        val updateAppointmentInRoom = withContext(Dispatchers.IO) {
            updateAppointmentInRoomUseCase.invoke(validatedAppointment)
        }

        if (updateAppointmentInRoom is Resource.Success) {
            val oldAppointment = getAppointmentFromCacheByIdUseCase.invoke(validatedAppointment.id)
            if (oldAppointment is Resource.Error) {
                logger.warn("Old appointment doest not exist in cache anymore, changing to create operation instead an update.")
                return addAppointmentToCacheUseCase.invoke(validatedAppointment)
            } else {
                if (!validatedAppointment.compareAppointmentDates(oldAppointment.data!!)) {
                    val oldDatesList = getDatesFromCacheByAppointmentUseCase.invoke(validatedAppointment.id).data
                        ?: emptyList()
                    clearAndRegenerateDates(validatedAppointment, oldDatesList)
                    repository.updateAppointmentInCache(validatedAppointment)
                }
                logger.info("Appointment updated successfully.")
                return Resource.Success(true)
            }
        } else {
            logger.info("Successfully updated appointment in room.")
            return updateAppointmentInRoom
        }
    }

    private suspend fun clearAndRegenerateDates(
        validatedAppointment: Appointment,
        oldDatesList: List<Int>
    ) {
        oldDatesList.forEach {
            repository.deleteAppointmentFromByDateCache(it, validatedAppointment.id)
        }
        repository.clearDateByAppointment(validatedAppointment.id)

        val dateRange = generateSequence(validatedAppointment.startDate) { current ->
            if (current.isBefore(validatedAppointment.endDate) || current.isEqual(validatedAppointment.endDate))
                current.plusDays(1)
            else null
        }

        dateRange.forEach { date ->
            val intDate = repository.generateDateKey(date.dayOfMonth, date.monthValue, date.year)
            repository.addDateToByAppointmentCache(validatedAppointment.id, intDate)
        }
    }
}