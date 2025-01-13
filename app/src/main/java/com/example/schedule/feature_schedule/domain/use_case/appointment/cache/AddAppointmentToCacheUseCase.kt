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
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddAppointmentToCacheUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase,
    private val addAppointmentToRoomUseCase: AddAppointmentToRoomUseCase
) {
    suspend operator fun invoke(appointment: Appointment): Resource<Boolean> {
        val result = validateAppointmentInfosUseCase.invoke(appointment, false)
        if (result is Resource.Error) return Resource.Error(result.message ?: "Validation failed")
        val appointmentToCreate = result.data!!

        return try {
            val createAppointmentInRoom = withContext(Dispatchers.IO) {
                return@withContext addAppointmentToRoomUseCase.invoke(appointmentToCreate).data
            }

            if (createAppointmentInRoom == true) {
                repository.addAppointmentToCache(appointmentToCreate)
                val dateRange = generateSequence(appointment.startDate) { current ->
                    if (current.isBefore(appointment.endDate) || current.isEqual(appointment.endDate)) current.plusDays(
                        1
                    ) else null
                }
                dateRange.forEach { date ->
                    val key = repository.generateDateKey(date.dayOfMonth, date.monthValue, date.year)
                    repository.addAppointmentToByDateCache(key, appointment.id)
                    repository.addDateToByAppointmentCache(appointment.id, key)
                }
                Resource.Success(true)
            } else {
                Resource.Error("Failed to create appointment in Room.")
            }
        } catch (e: IOException) {
            return Resource.Error("IO Exception: ${e.message}")
        } catch (e: Exception) {
            return Resource.Error("Exception: ${e.message}")
        }
    }
}

//generateSequence(appointment.startDate) { current ->
//    if (current.isBefore(appointment.endDate) || current.isEqual(appointment.endDate)) current.plusDays(1) else null
//}.forEach { date ->
//    val key = repository.generateDateKey(date.dayOfMonth, date.monthValue, date.year)
//
//    appointmentByDayCache.computeIfAbsent(key) { mutableListOf() }.add(appointment.id)
//}