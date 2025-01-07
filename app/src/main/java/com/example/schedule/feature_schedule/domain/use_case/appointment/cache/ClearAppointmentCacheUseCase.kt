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
        try {
            repository.clearCache()
            return Resource.Success(true)
        } catch (e: IOException) {
            return Resource.Error("IO Exception: $e")
        } catch (e: Exception) {
            return Resource.Error("Exception: $e")
        }
    }
}