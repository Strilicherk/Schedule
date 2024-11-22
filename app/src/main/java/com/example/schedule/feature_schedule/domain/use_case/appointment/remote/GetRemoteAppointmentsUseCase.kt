package com.example.schedule.feature_schedule.domain.use_case.appointment.remote

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpsertRemoteAppointmentsIntoRoomUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetRemoteAppointmentsUseCase @Inject constructor(
    private val postUnsyncedAppointmentsUseCase: PostUnsyncedAppointmentsUseCase,
    private val upsertRemoteAppointmentsIntoRoomUseCase: UpsertRemoteAppointmentsIntoRoomUseCase,
    private val repository: AppointmentRepository
) {
    operator fun invoke(): Flow<Resource<List<Appointment>>> {
        return flow {
            emit(Resource.Loading())

            val postUnsyncedResult = postUnsyncedAppointmentsUseCase.invoke().firstOrNull()

            when (postUnsyncedResult) {
                is Resource.Success -> {
                    if (postUnsyncedResult.data == true) {
                        try {
                            val remoteAppointments = repository.syncRemoteAppointments()
                            upsertRemoteAppointmentsIntoRoomUseCase.invoke(remoteAppointments)
                            emit(Resource.Success(remoteAppointments))
                        } catch (e: IOException) {
                            e.printStackTrace()
                            emit(Resource.Error("I/O error: ${e.message}"))
                        } catch (e: HttpException) {
                            e.printStackTrace()
                            emit(Resource.Error("Network error: ${e.message()}"))
                        }
                    } else {
                        emit(Resource.Error("Posting unsynced appointments failed."))
                    }
                }
                is Resource.Error -> {
                    emit(Resource.Error("Failed to post unsynced appointments: ${postUnsyncedResult.message}"))
                }
                else -> {
                    emit(Resource.Error("Unknown error occurred during posting unsynced appointments."))
                }
            }
        }
    }
}