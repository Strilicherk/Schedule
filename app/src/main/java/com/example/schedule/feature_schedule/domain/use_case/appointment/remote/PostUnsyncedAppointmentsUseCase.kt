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

            val res = repository.postUnsyncedRemoteAppointments(unsyncedLocalAppointments)
            val successSync = mutableListOf<Int>()
            res.filter { (key, value) ->
                if (value.toString().toBoolean()) {
                    successSync.add(key)
                    true
                } else {
                    false
                }
            }

            if (res.size == successSync.size) {
                emit(Resource.Success(true))
            } else {
                emit(Resource.Error("Failed to sync some appointments", false))
            }
            return@flow
        }
    }
}