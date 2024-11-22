package com.example.schedule.feature_schedule.data.repository

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.data.data_source.local.AppointmentDatabase
import com.example.schedule.feature_schedule.data.data_source.remote.AppointmentApi
import com.example.schedule.feature_schedule.data.mapper.AppointmentMapper.domainToEntity
import com.example.schedule.feature_schedule.data.mapper.AppointmentMapper.dtoToEntity
import com.example.schedule.feature_schedule.data.mapper.AppointmentMapper.entityToDomain
import com.example.schedule.feature_schedule.data.mapper.AppointmentMapper.entityToDto
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import retrofit2.Response

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentRepositoryImpl @Inject constructor(
    private val api: AppointmentApi,
    private val db: AppointmentDatabase
) : AppointmentRepository {
    private val dao = db.dao

    // local
    override suspend fun selectAppointments(): List<Appointment> {
        return dao.selectAppointments().map { it.entityToDomain() }
    }

    override suspend fun selectLocalAppointmentsOfTheYear(startOfYear: Long, endOfYear: Long): List<Appointment> {
        return dao.selectAppointmentsOfTheYear(startOfYear, endOfYear).map { it.entityToDomain() }
    }

    override suspend fun selectUnsyncedLocalAppointments(): List<Appointment> {
        return dao.selectUnsyncedAppointments().map { it.entityToDomain() }
    }

    override suspend fun upsertLocalAppointment(appointmentList: List<Appointment>) {
        return appointmentList.forEach { dao.upsertAppointment(it.domainToEntity()) }
    }

    override suspend fun deleteLocalAppointment(idList: List<Int>) {
        return idList.forEach { dao.deleteAppointment(it) }
    }

    // remote
    override suspend fun getRemoteAppointments(): List<Appointment> {
        return api.getAppointments().map { it.dtoToEntity().entityToDomain()}
    }

    override suspend fun postUnsyncedRemoteAppointments(appointmentList: List<Appointment>): Response<Any> {
        return api.postAppointments(appointmentList.map { it.domainToEntity().entityToDto() })
    }

    override suspend fun deleteRemoteAppointments(idList: List<Int>): Response<Any> {
        for (id in idList) {
            return api.deleteAppointment(id)
        }
        return Response.success("All appointments deleted")
    }
}
