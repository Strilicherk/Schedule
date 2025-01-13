package com.example.schedule.feature_schedule.domain.use_case.appointment.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeleteAppointmentFromRoomUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(id: Int): Resource<Boolean> {
    }
}