package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.DeleteAppointmentFromRoomUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteAppointmentFromCacheUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val deleteAppointmentFromRoomUseCase: DeleteAppointmentFromRoomUseCase,
    private val addAppointmentToCacheUseCase: AddAppointmentToCacheUseCase
) {
    suspend operator fun invoke(appointment: Appointment): Resource<Boolean> {
        val dateList = repository.getDatesByAppointment(appointment.id)

        if (dateList.isEmpty())
            return Resource.Error("No dates found for appointment ID: ${appointment.id}")

        return try {
            val response = withContext(Dispatchers.IO) {
                deleteAppointmentFromRoomUseCase.invoke(appointment.id)
            }

            return if (response is Resource.Error) {
                Resource.Error("Cannot delete appointment from database")
            } else {
                try {
                    coroutineScope {
                        val deleteByDay = async {
                            dateList.forEach { repository.deleteAppointmentFromByDateCache(it, appointment.id) }
                        }
                        val deleteFromCache = async {
                            repository.deleteAppointmentFromCache(appointment)
                        }
                        deleteByDay.await()
                        deleteFromCache.await()
                    }
                    Resource.Success(true)
                } catch (e: Exception) {
                    addAppointmentToCacheUseCase.invoke(appointment)
                    Resource.Error("Failed to delete appointment from cache: ${e.message}")
                }
            }
        } catch (e: IOException) {
            Resource.Error("IO Exception: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message}")
        }
    }
}