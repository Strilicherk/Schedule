package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAppointmentFromCacheByIdUseCase @Inject constructor(
    private val repository: AppointmentRepository
){
    suspend operator fun invoke(id: Int): Resource<Appointment> {
        return try {
            Resource.Success(repository.getAppointmentById(id))
        } catch (e: IOException) {
            Resource.Error("IO Exception: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message}")
        }
    }
}