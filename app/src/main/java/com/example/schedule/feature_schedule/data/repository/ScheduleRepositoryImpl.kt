package com.example.schedule.feature_schedule.data.repository

import com.example.schedule.feature_schedule.data.data_source.ScheduleDao
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.ScheduleRepository
import kotlinx.coroutines.flow.Flow

class ScheduleRepositoryImpl(
    private val dao: ScheduleDao
): ScheduleRepository {
    override fun getAppointments(): Flow<List<Appointment>> {
        return dao.getAppointments()
    }

    override suspend fun insertOrUpdateAppointment(appointment: Appointment) {
        return dao.insertOrUpdateAppointment(appointment)
    }

    override suspend fun deleteAppointment(appointment: Appointment) {
        return dao.deleteAppointment(appointment)
    }

}