package com.example.schedule.feature_schedule.domain.repository

import com.example.schedule.feature_schedule.domain.model.Appointment
import kotlinx.coroutines.flow.Flow

interface ScheduleRepository {
    fun getAppointments(): Flow<List<Appointment>>
    suspend fun insertOrUpdateAppointment(appointment: Appointment)
    suspend fun deleteAppointment(appointment: Appointment)
}