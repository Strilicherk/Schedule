package com.example.schedule.feature_schedule.domain.use_case.appointment

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpsertRemoteAppointmentsIntoRoomUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(apiAppointmentList: Flow<Resource<List<Appointment>>>): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())
            when (val apiAppointments = apiAppointmentList.firstOrNull()) {
                is Resource.Success -> {
                    val remoteAppointments = apiAppointments.data
                    if (remoteAppointments!!.isNotEmpty()) {
                        val localAppointments = repository.getAllAppointmentsFromRoom()

                        val appointmentsWithConflict = remoteAppointments.filter { api ->
                            localAppointments.any { local -> local.id == api.id } ||
                                    localAppointments.any { local -> local.id == api.id && api.lastModified > local.lastModified }
                        }

                        val appointmentsToSync = remoteAppointments.filter { api ->
                            localAppointments.none { local -> local.id == api.id } ||
                                    localAppointments.any { local -> local.id == api.id && api.lastModified > local.lastModified }
                        }

                        withContext(Dispatchers.IO) {
                            appointmentsToSync.forEach { repository.updateAppointmentInRoom(it) }
                        }

                        emit(Resource.Success(true))
                    } else {
                        println("correto")
                        emit(Resource.Success(true))
                    }
                }

                is Resource.Error -> {
                    emit(Resource.Error(apiAppointments.message ?: "Unknown error occurred"))
                }

                else -> {
                    emit(Resource.Error("Failed to fetch appointments from API"))
                }
            }
        }
    }
}
