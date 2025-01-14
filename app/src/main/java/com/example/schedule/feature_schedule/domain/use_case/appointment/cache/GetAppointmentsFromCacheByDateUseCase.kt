package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAppointmentsFromCacheByDateUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(date: Int): Resource<List<Appointment>> {
        logger.info("Fetching appointments from cache by date: $date")
        val appointmentList = emptyList<Appointment>()
        val appointmentIdList = repository.getAppointmentListByDate(date).data!!
        if (appointmentIdList.isEmpty())
            return Resource.Success(emptyList())
        appointmentIdList.forEach {
            appointmentList.plus(repository.getAppointmentById(it))
        }
        logger.info("Appointments fetched successfully.")
        return Resource.Success(appointmentList)
    }
}