package com.example.schedule.feature_schedule.domain.use_case.appointment.sync

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.DeleteAppointmentFromRemoteUseCase
import org.slf4j.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProcessDeletionsUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val deleteAppointmentFromRemoteUseCase: DeleteAppointmentFromRemoteUseCase,
    private val logger: Logger
){
    suspend operator fun invoke(): Resource<Boolean> {
        logger.info("Processing deletions from cache")
        val appointmentToDeleteMap = repository.getAppointmentsFromDeleteList()

        if (appointmentToDeleteMap is Resource.Error) {
            logger.warn("Failed to get delete cache: ${appointmentToDeleteMap.message}")
            return Resource.Error("${appointmentToDeleteMap.message}")
        }

        appointmentToDeleteMap.data?.forEach { (id, value) ->
            if (value) {
                val response = deleteAppointmentFromRemoteUseCase.invoke(id, value)
                if (response is Resource.Success) {
                    logger.info("Deleted appointment $id successfully")
                    repository.deleteAppointmentFromDeleteCache(id)
                } else {
                    logger.warn("Failed to delete appointment $id")
                }
            }
        }

        return Resource.Success(true)
    }
}