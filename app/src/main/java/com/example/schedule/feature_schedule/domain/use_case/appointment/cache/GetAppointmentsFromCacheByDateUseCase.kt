package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAppointmentsFromCacheByDateUseCase @Inject constructor(
    private val repository: AppointmentRepository
){
    suspend operator fun invoke(date: Int): Resource<List<Appointment>> {
        val appointmentList = emptyList<Appointment>()
        return try {
            val appointmentIdList = repository.getAppointmentListByDate(date)
            if (appointmentIdList.isEmpty()) return Resource.Success(emptyList())

            appointmentIdList.forEach {
                appointmentList.plus(repository.getAppointmentById(it))
            }
            Resource.Success(appointmentList)
        } catch (e: IOException) {
            Resource.Error("IO Exception: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message}")
        }
    }
}