package com.example.schedule.feature_schedule.domain.use_case.appointment.sync

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpdateAppointmentInRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.AddAppointmentToRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetAppointmentsFromRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.UpdateAppointmentInRemoteUseCase
import org.slf4j.Logger
import javax.inject.Inject

class UploadAppointmentsUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val getAppointmentsFromRemoteUseCase: GetAppointmentsFromRemoteUseCase,
    private val upsertUnsyncedAppointmentsToRemoteUseCase: UpsertUnsyncedAppointmentsToRemoteUseCase,
    private val processDeletionsUseCase: ProcessDeletionsUseCase,
    private val logger: Logger
) {
    suspend operator fun invoke(): Resource<Boolean> {
        logger.info("Starting sync process")

        // Step 1: Delete appointments
        logger.info("Processing deletions")
        val deleteResult = processDeletionsUseCase.invoke()
        if (deleteResult is Resource.Success) {
            logger.info("Deletion process completed.")
        }

        // Step 2: Fetch data from API
        logger.info("Fetching appointments from remote API")
        val appointmentsFromRemoteList = getAppointmentsFromRemoteUseCase.invoke()
        if (appointmentsFromRemoteList is Resource.Error) {
            logger.error("Failed to fetch appointments from API: ${appointmentsFromRemoteList.message}")
            return Resource.Error("Failed to fetch appointments from API")
        }

        // Step 3: Get unsynced appointments from room
        logger.info("Retrieving unsynced appointments from local storage")
        val unsyncedAppointments = repository.getUnsyncedAppointmentsFromRoom()
        if (unsyncedAppointments is Resource.Error) {
            logger.error("Failed to retrieve unsynced appointments: ${unsyncedAppointments.message}")
            return Resource.Error("Failed to retrieve unsynced appointments")
        }

        if (unsyncedAppointments.data.isNullOrEmpty()) {
            logger.info("No unsynced appointments found")
        } else {
            val upsertResult = upsertUnsyncedAppointmentsToRemoteUseCase.invoke(
                unsyncedAppointments.data,
                appointmentsFromRemoteList.data ?: emptyList()
            )
            if (upsertResult is Resource.Error) {
                return Resource.Error("${upsertResult.message}")
            }
        }

        logger.info("Synchronization successfully completed")
        return Resource.Success(true)
    }
}