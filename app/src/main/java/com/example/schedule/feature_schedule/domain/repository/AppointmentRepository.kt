package com.example.schedule.feature_schedule.domain.repository

import com.example.schedule.feature_schedule.domain.model.Appointment
import okhttp3.Response

interface AppointmentRepository {
    // API
    suspend fun syncRemoteAppointments(): List<Appointment>
    suspend fun postUnsyncedRemoteAppointments(appointmentList: List<Appointment>): Response
    suspend fun updateRemoteAppointments(appointmentList: List<Appointment>): Response
    suspend fun deleteRemoteAppointments(appointmentList: List<Appointment>): Response

    // DATABASE
    suspend fun selectAppointments(): List<Appointment>
    suspend fun selectLocalAppointmentsOfTheYear(): List<Appointment>
    suspend fun selectUnsyncedLocalAppointments(): List<Appointment>
    suspend fun upsertLocalAppointment(appointment: Appointment)
    suspend fun deleteLocalAppointment(appointment: Appointment)

}