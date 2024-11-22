package com.example.schedule.feature_schedule.domain.use_case.appointment.local

import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import javax.inject.Inject

class DeleteLocalAppointmentUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(idList: List<Int>) {
        return repository.deleteLocalAppointment(idList)
    }
}