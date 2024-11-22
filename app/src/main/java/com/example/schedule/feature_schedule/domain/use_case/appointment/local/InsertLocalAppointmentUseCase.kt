package com.example.schedule.feature_schedule.domain.use_case.appointment.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow

class InsertLocalAppointmentUseCase(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(appointmentList: List<Appointment>) {
        repository.upsertLocalAppointment(appointmentList)
    }
}