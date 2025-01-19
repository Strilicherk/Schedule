package com.example.schedule.feature_schedule.domain.use_case.appointment.sync

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.AddAppointmentToCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.ClearAppointmentCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.AddAppointmentToRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.ClearRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetAppointmentsFromRemoteUseCase
import org.slf4j.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadAppointmentsUseCase @Inject constructor(
    private val getAppointmentsFromRemoteUseCase: GetAppointmentsFromRemoteUseCase,
    private val clearRoomUseCase: ClearRoomUseCase,
    private val addAppointmentToCacheUseCase: AddAppointmentToCacheUseCase,
    private val logger: Logger
) {
    suspend operator fun invoke(): Resource<Boolean> {
        logger.info("Starting data download process")

        // Step 1: Fetch data from API
        logger.info("Fetching appointments from remote API")
        val appointmentsFromRemoteList = getAppointmentsFromRemoteUseCase.invoke()
        if (appointmentsFromRemoteList is Resource.Error) {
            logger.error("Failed to fetch appointments from API: ${appointmentsFromRemoteList.message}")
            return Resource.Error("Failed to fetch appointments from API")
        }

        // Step 2: Clear local database (Room)
        logger.info("Clearing local storage")
        val clearRoomResult = clearRoomUseCase.invoke()
        if (clearRoomResult is Resource.Error) {
            logger.error("Failed to clear local storage: ${clearRoomResult.message}")
            return Resource.Error("Failed to clear local storage")
        }
        logger.info("Successfully cleared local storage")

        // Step 3: Insert data into room and cache
        logger.info("Inserting ${appointmentsFromRemoteList.data!!.size} appointments into into room and cache")
        appointmentsFromRemoteList.data.forEach { appointment ->
            val insertResult =
                addAppointmentToCacheUseCase.invoke(appointment) // Inside this use case, another use case is called to insert data in Room
            if (insertResult is Resource.Success) {
                logger.info("Successfully inserted appointment with ID: ${appointment.id}")
            } else {
                logger.warn("Failed to insert appointment with ID: ${appointment.id}")
            }
        }
        logger.info("Data download process successfully completed")
        return Resource.Success(true)
    }
}