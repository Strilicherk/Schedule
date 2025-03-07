package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.DeleteAppointmentFromRoomUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteAppointmentFromCacheUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val deleteAppointmentFromRoomUseCase: DeleteAppointmentFromRoomUseCase,
    private val addAppointmentToCacheUseCase: AddAppointmentToCacheUseCase,
    private val logger: Logger
) {
    suspend operator fun invoke(appointment: Appointment): Resource<Boolean> {
        logger.info("Deleting appointment from cache.")
        val dateList = repository.getDatesByAppointment(appointment.id).data!!
        if (dateList.isEmpty()) {
            logger.error("No dates found for appointment ID: ${appointment.id}")
            return Resource.Error("No dates found for appointment ID: ${appointment.id}")
        }

        val response = withContext(Dispatchers.IO) {
            deleteAppointmentFromRoomUseCase.invoke(appointment.id)
        }

        if (response is Resource.Error) {
            logger.error(response.message)
            return response
        }

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
            repository.addAppointmentToDeleteCache(appointment.id, appointment.hasBeenSynced)
            logger.info("Appointment successfully deleted from cache.")
            return Resource.Success(true)
        } catch (e: Exception) {
            addAppointmentToCacheUseCase.invoke(appointment)
            logger.error("Failed to delete appointment from cache: ${e.message}")
            return Resource.Error("Failed to delete appointment from cache: ${e.message}")
        }
    }
}