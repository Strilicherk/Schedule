package com.example.schedule.feature_schedule.domain.use_case.appointment.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.slf4j.Logger
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllAppointmentsFromRoomUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(): Resource<List<Appointment>> {
        logger.info("Fetching all appointments from Room")
        return repository.getAllAppointmentsFromRoom()
    }
}