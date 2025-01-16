package com.example.schedule.feature_schedule.domain.use_case.appointment.remote

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import org.slf4j.Logger
import javax.inject.Inject

class DeleteAppointmentFromRemoteUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(id: Int, hasBeenSynced: Boolean): Resource<Boolean> {
        logger.info("Verifying appointment to delete")
        if (!hasBeenSynced) {
            logger.error("Appointment is not synced yet")
            return Resource.Error("This appointment can't be deleted in remote because it was not synced")
        }

        val response = repository.deleteRemoteAppointment(id)
        if (response is Resource.Success) logger.info("Successfully deleted appointment $id from remote.")
        else logger.error("Failed to delete appointment $id from remote")
        return response
    }
}
