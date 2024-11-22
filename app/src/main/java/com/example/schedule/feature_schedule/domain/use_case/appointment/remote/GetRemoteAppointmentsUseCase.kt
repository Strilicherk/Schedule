package com.example.schedule.feature_schedule.domain.use_case.appointment.remote

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRemoteAppointmentsUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    operator fun invoke(): Flow<Resource<List<Appointment>>> {
        return flow {
            emit(Resource.Loading())

            try {
                val remoteAppointments = repository.getRemoteAppointments()
                emit(Resource.Success(remoteAppointments))
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("I/O error: ${e.message}"))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Network error: ${e.message()}"))
            }
        }
    }
}