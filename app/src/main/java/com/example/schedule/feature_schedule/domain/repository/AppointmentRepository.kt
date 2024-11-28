package com.example.schedule.feature_schedule.domain.repository

import com.example.schedule.feature_schedule.domain.model.Appointment
import retrofit2.Call
import retrofit2.Response

interface AppointmentRepository {
    // local
    suspend fun selectAppointments(): List<Appointment>
    suspend fun selectLocalAppointmentsOfTheYear(startOfYear: Long, endOfYear: Long): List<Appointment>
    suspend fun selectUnsyncedLocalAppointments(): List<Appointment>
    suspend fun upsertLocalAppointment(appointmentList: List<Appointment>)
    suspend fun deleteLocalAppointment(idList: List<Int>)

    // remote
    suspend fun getRemoteAppointments(): List<Appointment>
    suspend fun postUnsyncedRemoteAppointments(appointmentList: List<Appointment>): MutableMap<Appointment, Boolean>
    suspend fun deleteRemoteAppointments(idList: List<Int>): MutableMap<Int, Boolean>
}