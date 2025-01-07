package com.example.schedule.feature_schedule.domain.use_case.appointment.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpsertLocalAppointmentUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(appointment: Appointment): Flow<Resource<Boolean>>{
        return flow {
            emit(Resource.Loading())
            try {
                repository.upsertLocalAppointment(appointment)
                emit(Resource.Success(true))
            } catch (e: IOException) {
                emit(Resource.Error("IO Exception: ${e.message}"))
            } catch (e: Exception) {
                emit(Resource.Error("Exception: ${e.message}"))
            }
        }
    }
}