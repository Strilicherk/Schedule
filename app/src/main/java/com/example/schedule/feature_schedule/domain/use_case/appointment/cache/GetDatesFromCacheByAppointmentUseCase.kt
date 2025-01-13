package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetDatesFromCacheByAppointmentUseCase @Inject constructor(
    private val repository: AppointmentRepository
){
    suspend operator fun invoke(id: Int): Resource<List<Int>> {
        return try {
            Resource.Success(repository.getDatesByAppointment(id))
        } catch (e: IOException) {
            return Resource.Error("IO Exception: ${e.message}")
        } catch (e: Exception) {
            return Resource.Error("Exception: ${e.message}")
        }
    }
}