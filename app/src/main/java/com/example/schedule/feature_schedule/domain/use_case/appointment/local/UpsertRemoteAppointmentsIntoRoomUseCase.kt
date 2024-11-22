package com.example.schedule.feature_schedule.domain.use_case.appointment.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpsertRemoteAppointmentsIntoRoomUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(apiAppointmentList: Flow<Resource<List<Appointment>>>): Flow<Resource<Boolean>> {
        return flow {
            emit(Resource.Loading())

            val apiAppointments = apiAppointmentList.firstOrNull()

            when (apiAppointments) {
                is Resource.Success -> {
                    val remoteAppointments = apiAppointments.data
                    if (remoteAppointments!!.isNotEmpty()) {
                        val localAppointments = repository.selectAppointments()

                        val appointmentsToSync = remoteAppointments.filter { api ->
                            localAppointments.none { local -> local.id == api.id } || // Novo
                                    localAppointments.any { local -> local.id == api.id && api.lastModified > local.lastModified } // Atualizado
                        }

                        appointmentsToSync.forEach { repository.upsertLocalAppointment(it) }

                        emit(Resource.Success(true))
                    } else {
                        emit(Resource.Success(false)) // Nada a sincronizar
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
