package com.example.schedule.feature_schedule.domain.use_case.appointment.sync

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpdateAppointmentInRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.AddAppointmentToRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.UpdateAppointmentInRemoteUseCase
import org.slf4j.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpsertUnsyncedAppointmentsToRemoteUseCase @Inject constructor(
    private val updateAppointmentInRemoteUseCase: UpdateAppointmentInRemoteUseCase,
    private val updateAppointmentInRoomUseCase: UpdateAppointmentInRoomUseCase,
    private val addAppointmentToRemoteUseCase: AddAppointmentToRemoteUseCase,
    private val logger: Logger,
) {
    suspend operator fun invoke(
        unsyncedAppointments: List<Appointment>,
        appointmentsFromRemoteList: List<Appointment>
    ): Resource<Boolean> {
        // Update appointments in API
        val syncedAppointments = unsyncedAppointments.filter { it.hasBeenSynced }
        logger.info("Updating ${syncedAppointments.size} appointments in remote API")

        syncedAppointments.forEach { appointment ->
            val remoteAppointment = appointmentsFromRemoteList.find { it.id == appointment.id }
            if (remoteAppointment != null) {
                if (appointment.lastModified > remoteAppointment.lastModified) {
                    val response = updateAppointmentInRemoteUseCase.invoke(appointment)
                    if (response is Resource.Success) {
                        updateAppointmentInRoomUseCase(appointment.copy(isSynced = true))
                        logger.info("Successfully updated appointment ${appointment.id}")
                    } else
                        logger.warn("Failed to update appointment ${appointment.id}")
                } else
                    logger.info("Skipping update for appointment ${appointment.id} as it is already up to date.")
            } else
                logger.warn("Appointment ${appointment.id} not found in remote list.")
        }

        // Create new appointments in API
        val appointmentsToCreate = unsyncedAppointments.filter { !it.hasBeenSynced }
        logger.info("Creating ${appointmentsToCreate.size} new appointments in remote API")

        appointmentsToCreate.forEachIndexed { index, unsyncedAppointment ->
            val isConflicted =
                appointmentsFromRemoteList.any { it.id == unsyncedAppointment.id }
            val uniqueId = if (isConflicted) {
                appointmentsFromRemoteList.size + index + 1
            } else {
                unsyncedAppointment.id
            }

            val newAppointment = unsyncedAppointment.copy(id = uniqueId)
            val response = addAppointmentToRemoteUseCase.invoke(newAppointment)
            if (response is Resource.Success) {
                logger.info("Successfully created appointment with ID $uniqueId")
            } else {
                logger.warn("Failed to create appointment ${unsyncedAppointment.id}")
                return Resource.Error("${response.message}")
            }
        }

        return Resource.Success(true)
    }
}

