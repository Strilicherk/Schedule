package com.example.schedule.feature_schedule.data.data_source.local

import androidx.room.Dao
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
    suspend fun upsertAppointment(appointment: AppointmentEntity): Int

    @Query("DELETE FROM APPOINTMENT WHERE Id = :id")
    suspend fun deleteAppointment(id: Int): Int

//    @Query("SELECT MAX(id) FROM APPOINTMENT")
//    suspend fun getLastIdFromRoom(): Int
}