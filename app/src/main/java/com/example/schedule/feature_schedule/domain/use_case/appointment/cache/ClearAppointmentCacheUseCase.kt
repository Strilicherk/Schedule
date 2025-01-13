package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClearAppointmentCacheUseCase @Inject constructor(
    private val repository: AppointmentRepository
){
    suspend operator fun invoke(): Resource<Boolean> {
        return try {
            repository.clearCache()
        } catch (e: IOException) {
            Resource.Error("IO Exception: $e")
        } catch (e: Exception) {
            Resource.Error("Exception: $e")
        }
    }
}