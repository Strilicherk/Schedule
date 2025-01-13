package com.example.schedule.feature_schedule.domain.use_case.appointment.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddAppointmentToRoomUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(appointment: Appointment): Resource<Boolean> {
        return try {
            repository.upsertLocalAppointment(appointment)
            Resource.Success(true)
        } catch (e: IOException) {
            Resource.Error("IO Exception: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("General Exception: ${e.message}")
        }
    }
}
