package com.example.schedule.feature_schedule.domain.repository

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment

interface AppointmentRepository {
    // local
    suspend fun addAppointmentToRoom(appointment: Appointment): Resource<Boolean>
    suspend fun getAllAppointmentsFromRoom(): Resource<List<Appointment>>
    suspend fun getUnsyncedAppointmentsFromRoom(): Resource<List<Appointment>>
    suspend fun updateAppointmentInRoom(appointment: Appointment): Resource<Boolean>
    suspend fun deleteAppointmentFromRoom(id: Int): Resource<Int>

    // remote
    suspend fun addAppointmentToRemote(appointment: Appointment): Resource<Boolean>
    suspend fun getRemoteAppointments(): Resource<List<Appointment>>
    suspend fun deleteRemoteAppointment(id: Int): Resource<Boolean>
    suspend fun updateRemoteAppointment (appointment: Appointment): Resource<Boolean>

    // cache
    suspend fun addAppointmentToCache(appointment: Appointment): Resource<Boolean>
    suspend fun addAppointmentToByDateCache(date: Int, appointmentId: Int): Resource<Boolean>
    suspend fun addDateToByAppointmentCache(appointmentId: Int, date: Int): Resource<Boolean>
    suspend fun addAppointmentToDeleteCache(id: Int, hasBeenSynced: Boolean): Resource<Boolean>
    suspend fun getAllAppointmentsFromCache(): Resource<Map<Int, Appointment>>
    suspend fun getAllAppointmentsFromByDateCache(): Resource<Map<Int, List<Int>>>
    suspend fun getAppointmentById(id: Int): Resource<Appointment>
    suspend fun getDatesByAppointment(id: Int): Resource<List<Int>>
    suspend fun getLastIdInCache(): Resource<Int>
    suspend fun getAppointmentListByDate(id: Int): Resource<List<Int>>
    suspend fun getAppointmentsFromDeleteList(): Resource<Map<Int,Boolean>>
    suspend fun updateAppointmentInCache(appointment: Appointment): Resource<Boolean>
    suspend fun deleteAppointmentFromCache(appointment: Appointment): Resource<Boolean>
    suspend fun deleteAppointmentFromDateCache(id: Int): Resource<Boolean>
    suspend fun deleteAppointmentFromByDateCache(date: Int, id: Int): Resource<Boolean>
    suspend fun deleteAppointmentFromDeleteCache(id: Int): Resource<Boolean>
    suspend fun clearCache(): Resource<Boolean>
    suspend fun clearDateByAppointment(id: Int): Resource<Boolean>
    suspend fun generateDateKey(day: Int, month: Int, year: Int): Int
}