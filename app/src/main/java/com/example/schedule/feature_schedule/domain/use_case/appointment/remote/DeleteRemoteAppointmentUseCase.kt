package com.example.schedule.feature_schedule.domain.use_case.appointment.remote

import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import javax.inject.Inject

class DeleteRemoteAppointmentUseCase @Inject constructor(
    val repository: AppointmentRepository
) {
    operator fun invoke() {

    }
}