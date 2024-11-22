package com.example.schedule.feature_schedule.domain.use_case.appointment.remote

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class PostUnsyncedAppointmentsUseCase(
    private val repository: AppointmentRepository
) {
    operator fun invoke(): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())
            val unsyncedLocalAppointments = repository.selectUnsyncedLocalAppointments()

            if (unsyncedLocalAppointments.isEmpty()) {
                emit(Resource.Success(true))
                return@flow
            }

            try {
                repository.postUnsyncedRemoteAppointments(unsyncedLocalAppointments)
                emit(Resource.Success(true))
                return@flow
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Network error: ${e.message()}"))
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("I/O error: ${e.message}"))
            }
        }
    }
}