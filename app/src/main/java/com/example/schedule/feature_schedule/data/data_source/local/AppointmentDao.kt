package com.example.schedule.feature_schedule.data.data_source.local

import androidx.room.*

@Dao
interface AppointmentDao {
    @Query("SELECT * FROM APPOINTMENT")
    fun selectAppointments(): List<AppointmentEntity>

    @Query("SELECT * FROM APPOINTMENT WHERE isSynced = 0")
    suspend fun selectUnsyncedAppointments(): List<AppointmentEntity>

    @Upsert
    suspend fun upsertAppointment(appointment: AppointmentEntity): Long

    @Query("DELETE FROM APPOINTMENT WHERE Id = :id")
    suspend fun deleteAppointment(id: Int): Int

    @Query("DELETE FROM APPOINTMENT")
    suspend fun clearAppointmentTable(): Int
}