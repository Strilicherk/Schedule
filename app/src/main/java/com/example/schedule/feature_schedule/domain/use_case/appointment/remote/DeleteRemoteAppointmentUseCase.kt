package com.example.schedule.feature_schedule.domain.use_case.appointment.remote

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class DeleteRemoteAppointmentUseCase @Inject constructor(
    val repository: AppointmentRepository,
) {
    operator fun invoke(appointmentsFromApi: Flow<Resource<List<Appointment>>>, appointmentsToDelete: List<Appointment>): Flow<Resource<List<Appointment>>> {
        return flow {
            emit(Resource.Loading())

            val appointmentsListFromApi = appointmentsFromApi.first().data ?: emptyList()

            if (appointmentsToDelete.isEmpty()) {
                emit(Resource.Success(appointmentsListFromApi))
                return@flow
            } else {

                val appointmentsToDeleteInApi = appointmentsToDelete.filter { local ->
                    appointmentsListFromApi.any { api -> api.id == local.id } && local.hasBeenSynced
                }

                if (appointmentsToDeleteInApi.isNotEmpty()) {
                    try {
                        withContext(Dispatchers.IO) {
                            repository.deleteRemoteAppointments(appointmentsToDelete.map { it.id })
                            val remoteAppointmentList = appointmentsListFromApi.filter { apiItem ->
                                appointmentsToDeleteInApi.map { it.id }.contains(apiItem.id)
                            }
                            emit(Resource.Success(remoteAppointmentList))
                        }
                    } catch (e: HttpException) {
                        e.printStackTrace()
                        emit(Resource.Error("Http Exception: ${e.code()} - ${e.message}"))
                    } catch (e: IOException) {
                        e.printStackTrace()
                        emit(Resource.Error("IO Exception: ${e.message}"))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        emit(Resource.Error("Exception: ${e.message}"))
                    }
                } else {
                    emit(Resource.Success(appointmentsListFromApi))
                }
            }
        }
    }
}