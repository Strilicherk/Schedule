package com.example.schedule.feature_schedule.domain.repository

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment

interface AppointmentRepository {
    // local
    suspend fun selectAppointments(): List<Appointment>
    suspend fun selectLocalAppointmentsOfTheYear(
        startOfYear: Long,
        endOfYear: Long
    ): List<Appointment>

    suspend fun selectUnsyncedLocalAppointments(): List<Appointment>
    suspend fun upsertLocalAppointment(appointment: Appointment): Boolean
    suspend fun deleteLocalAppointment(idList: List<Int>): Int
//    suspend fun getLastIdFromRoom(): Int

    // remote
    suspend fun getRemoteAppointments(): List<Appointment>
    suspend fun postUnsyncedRemoteAppointments(appointmentList: List<Appointment>): MutableMap<Int, Any>
    suspend fun deleteRemoteAppointments(idList: List<Int>): MutableMap<Int, Boolean>

    // cache
    suspend fun addAppointmentToCache(appointment: Appointment): Resource<Boolean>
    suspend fun addAppointmentToByDateCache(date: Int, appointmentId: Int): Resource<Boolean>
    suspend fun addDateToByAppointmentCache(appointmentId: Int, date: Int): Resource<Boolean>
    suspend fun addAppointmentToDeleteCache(id: Int, hasBeenSynced: Boolean): Resource<Boolean>
    suspend fun getAllAppointmentsFromCache(): Map<Int, Appointment>
    suspend fun getAllAppointmentsFromByDateCache(): Map<Int, List<Int>>
    suspend fun getAppointmentById(id: Int): Appointment
    suspend fun getDatesByAppointment(id: Int): List<Int>
    suspend fun getLastIdInCache(): Int
    suspend fun getAppointmentListByDate(id: Int): List<Int>
    suspend fun updateAppointmentInCache(appointment: Appointment): Resource<Boolean>
    suspend fun deleteAppointmentFromCache(appointment: Appointment): Resource<Boolean>
    suspend fun deleteAppointmentFromDateCache(id: Int): Resource<Boolean>
    suspend fun deleteAppointmentFromByDateCache(date: Int, id: Int): Resource<Boolean>
    suspend fun clearCache(): Resource<Boolean>
    suspend fun clearDateByAppointment(id: Int): Resource<Boolean>
    suspend fun generateDateKey(day: Int, month: Int, year: Int): Int
}