package com.example.schedule.feature_schedule.domain.repository

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import retrofit2.Call
import retrofit2.Response

interface AppointmentRepository {
    // local
    suspend fun selectAppointments(): List<Appointment>
    suspend fun selectLocalAppointmentsOfTheYear(
        startOfYear: Long,
        endOfYear: Long
    ): List<Appointment>
    suspend fun selectUnsyncedLocalAppointments(): List<Appointment>
    suspend fun upsertLocalAppointment(appointment: Appointment): Long
    suspend fun deleteLocalAppointment(idList: List<Int>): Int
    suspend fun getLastIdFromRoom(): Int

    // remote
    suspend fun getRemoteAppointments(): List<Appointment>
    suspend fun postUnsyncedRemoteAppointments(appointmentList: List<Appointment>): MutableMap<Int, Any>
    suspend fun deleteRemoteAppointments(idList: List<Int>): MutableMap<Int, Boolean>

    // cache
    suspend fun addAppointmentToCache(appointment: Appointment): Resource<Boolean>
    suspend fun getAllCachedAppointments(): Map<Long, List<Appointment>>
    suspend fun updateCachedAppointment(appointment: Appointment): Resource<Boolean>
    suspend fun clearAppointmentCache()
    suspend fun loadAppointmentsFromRepositoryToCache(): Resource<Boolean>
    suspend fun deleteAppointmentFromCache(appointment: Appointment): Resource<Boolean>
    suspend fun saveLastIdInCache(id: Int): Int
    suspend fun getLastIdInCache(): Int?
}