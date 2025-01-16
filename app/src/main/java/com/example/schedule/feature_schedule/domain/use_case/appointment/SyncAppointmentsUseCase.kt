package com.example.schedule.feature_schedule.domain.use_case.appointment

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpdateAppointmentInRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.AddAppointmentToRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.DeleteAppointmentFromRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetAppointmentsFromRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.UpdateAppointmentInRemoteUseCase
import org.slf4j.Logger
import javax.inject.Inject

class SyncAppointmentsUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val getAppointmentsFromRemoteUseCase: GetAppointmentsFromRemoteUseCase,
    private val updateAppointmentInRoomUseCase: UpdateAppointmentInRoomUseCase,
    private val deleteAppointmentFromRemoteUseCase: DeleteAppointmentFromRemoteUseCase,
    private val updateAppointmentInRemoteUseCase: UpdateAppointmentInRemoteUseCase,
    private val addAppointmentToRemoteUseCase: AddAppointmentToRemoteUseCase,
    private val logger: Logger
) {
    suspend operator fun invoke(): Resource<Boolean> {
        logger.info("Starting sync process")
        logger.info("Getting appointments from delete cache")
        val appointmentToDeleteMap = repository.getAppointmentsFromDeleteList()

        if (appointmentToDeleteMap is Resource.Error)
            logger.warn("Failed to get appointments from delete cache: ${appointmentToDeleteMap.message}")
        else if (appointmentToDeleteMap.data!!.isEmpty())
            logger.info("Appointment delete cache is empty")
        else appointmentToDeleteMap.data.forEach { appointment ->
            if (appointment.value) {
                val res =
                    deleteAppointmentFromRemoteUseCase.invoke(appointment.key, appointment.value)
                if (res is Resource.Success) {
                    logger.info("Appointment ${appointment.key} was successfully deleted in remote")
                    repository.deleteAppointmentFromDeleteCache(appointment.key)
                } else {
                    logger.warn("Failed to delete id: ${appointment.key} in remote.")
                }
            }
        }

        logger.info("Fetching data from API")
        val appointmentsFromRemoteList = getAppointmentsFromRemoteUseCase.invoke()
        if (appointmentsFromRemoteList is Resource.Error) {
            logger.error("Ending sync process because is not possible to fetch data from API")
            return Resource.Error(appointmentsFromRemoteList.message!!)
        }

        logger.info("Getting unsynced appointments from room")
        val unsyncedAppointments = repository.getUnsyncedAppointmentsFromRoom()

        if (unsyncedAppointments is Resource.Error) {
            logger.error("Failed to get unsynced appointments from room: ${unsyncedAppointments.message}")
            return Resource.Error("${unsyncedAppointments.message}")
        } else if (unsyncedAppointments.data!!.isEmpty()) {
            logger.info("No unsynced appointments found in room")
        } else {
            unsyncedAppointments.data.filter { appointment ->
                appointment.hasBeenSynced
            }.forEach { appointment ->
                val response = updateAppointmentInRemoteUseCase.invoke(appointment)
                if (response is Resource.Success) updateAppointmentInRoomUseCase(
                    appointment.copy(
                        isSynced = true
                    )
                )
            }

            unsyncedAppointments.data.filter { unsyncedAppointment ->
                !unsyncedAppointment.hasBeenSynced && appointmentsFromRemoteList.data?.any { remoteAppointment ->
                    remoteAppointment.id == unsyncedAppointment.id
                } == true
            }.forEach { unsyncedAppointment ->
                addAppointmentToRemoteUseCase.invoke(unsyncedAppointment.copy(id = (unsyncedAppointment.id + unsyncedAppointments.data.size)))
            }
        }

        logger.info("Sychronizing successfully completed")
        return Resource.Success(true)
    }
}