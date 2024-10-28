package com.example.schedule.feature_schedule.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import com.example.schedule.feature_schedule.domain.model.Appointment
import kotlinx.coroutines.flow.Flow


@Dao
interface ScheduleDao {
//    @Query("SELECT * FROM APPOINTMENT")
//    suspend fun getAllAppointments(): Flow<List<Appointment>>

    @Query("SELECT * FROM APPOINTMENT")
    fun getAppointments(): Flow<List<Appointment>>

    @Update
    suspend fun insertOrUpdateAppointment(appointment: Appointment)

    @Delete
    suspend fun deleteAppointment(appointment: Appointment)
}