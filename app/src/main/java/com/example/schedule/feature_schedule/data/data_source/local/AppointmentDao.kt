package com.example.schedule.feature_schedule.data.data_source.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface AppointmentDao {

    @Query("SELECT * FROM APPOINTMENT")
    fun selectAppointments(): List<AppointmentEntity>

    @Query(
        """SELECT * FROM APPOINTMENT
            WHERE (startDate BETWEEN :startOfYear AND :endOfYear) 
            OR (endDate BETWEEN :startOfYear AND :endOfYear)"""
    )
    fun selectAppointmentsOfTheYear(
        startOfYear: Long,
        endOfYear: Long
    ): List<AppointmentEntity>

    @Query("SELECT * FROM APPOINTMENT WHERE isSynced = 0")
    suspend fun selectUnsyncedAppointments(): List<AppointmentEntity>

    @Upsert
    suspend fun insertAppointment(appointment: AppointmentEntity)

    @Delete
    suspend fun deleteAppointment(appointment: AppointmentEntity)
}