package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.ValidateAppointmentInfosUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpdateAppointmentInRoomUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateAppointmentInCacheUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val updateAppointmentInRoomUseCase: UpdateAppointmentInRoomUseCase,
    private val getDatesFromCacheByAppointmentUseCase: GetDatesFromCacheByAppointmentUseCase,
    private val getAppointmentFromCacheByIdUseCase: GetAppointmentFromCacheByIdUseCase,
    private val validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase,
) {
    suspend operator fun invoke(appointment: Appointment): Resource<Boolean> {
        val validatedAppointment = validateAppointmentInfosUseCase.invoke(appointment, true).data
            ?: return Resource.Error("Validation failed")

        return try {
            val updateAppointmentInRoom = withContext(Dispatchers.IO) {
                updateAppointmentInRoomUseCase.invoke(validatedAppointment)
            }

            if (updateAppointmentInRoom is Resource.Success) {
                val oldAppointment =
                    getAppointmentFromCacheByIdUseCase.invoke(validatedAppointment.id).data
                        ?: return Resource.Error("Old appointment not found")

                if (!validatedAppointment.compareAppointmentDates(oldAppointment)) {
                    val oldDatesList =
                        getDatesFromCacheByAppointmentUseCase.invoke(validatedAppointment.id).data
                            ?: emptyList()

                    clearAndRegenerateDates(validatedAppointment, oldDatesList)
                    repository.updateAppointmentInCache(validatedAppointment)
                }

                Resource.Success(true)
            } else {
                updateAppointmentInRoom
            }
        } catch (e: IOException) {
            Resource.Error("IO Exception: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message}")
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
            if (current.isBefore(validatedAppointment.endDate) || current.isEqual(
                    validatedAppointment.endDate
                )
            )
                current.plusDays(1)
            else null
        }

        dateRange.forEach { date ->
            val intDate = repository.generateDateKey(date.dayOfMonth, date.monthValue, date.year)
            repository.addDateToByAppointmentCache(validatedAppointment.id, intDate)
        }
    }
}