package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import org.slf4j.Logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAppointmentsFromCacheByDateUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(date: Int): Resource<List<Appointment>> {
        logger.info("Fetching appointments from cache by date: $date")

        val appointmentIdListResource = repository.getAppointmentListByDate(date)

        if (appointmentIdListResource is Resource.Error) {
            logger.error(appointmentIdListResource.message)
            return Resource.Error(appointmentIdListResource.message ?: "Unknown Error")
        }

        val appointmentIdList = appointmentIdListResource.data

        if (appointmentIdList.isNullOrEmpty()) {
            logger.info("No appointments found for date: $date")
            return Resource.Success(emptyList())
        }

        val appointmentList = mutableListOf<Appointment>()
        for (id in appointmentIdList) {
            when (val result = repository.getAppointmentById(id)) {
                is Resource.Success -> appointmentList.add(result.data!!)
                else -> return Resource.Error(result.message ?: "Error fetching appointment with ID $id")
            }
        }

        logger.info("Appointments fetched successfully.")
        return Resource.Success(appointmentList)
    }
}