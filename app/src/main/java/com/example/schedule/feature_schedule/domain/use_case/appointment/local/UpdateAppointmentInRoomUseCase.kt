package com.example.schedule.feature_schedule.domain.use_case.appointment.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.ValidateAppointmentInfosUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateAppointmentInRoomUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase
) {
    suspend operator fun invoke(appointment: Appointment): Resource<Boolean> {
        val validatedAppointment = validateAppointmentInfosUseCase.invoke(appointment, true)
        return if (validatedAppointment is Resource.Success) {
            return try {
                val updatedAppointmentInRoom = withContext(Dispatchers.IO) {
                    return@withContext repository.upsertLocalAppointment(appointment)
                }
                if (updatedAppointmentInRoom) Resource.Success(true) else Resource.Error("Unable to update appointment in Room database")
            } catch (e: IOException) {
                return Resource.Error("IO Exception: ${e.message}")
            } catch (e: Exception) {
                return Resource.Error("Exception: ${e.message}")
            }
        } else {
            Resource.Error("${validatedAppointment.message}")
        }
    }
}