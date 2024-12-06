package com.example.schedule.feature_schedule.domain.use_case.appointment.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
                return@flow
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error("Exception: ${e.message}"))
                return@flow
            }
        }
    }
}