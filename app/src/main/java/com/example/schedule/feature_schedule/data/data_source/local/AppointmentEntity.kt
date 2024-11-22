package com.example.schedule.feature_schedule.data.data_source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Appointment")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val notes: String,
    val color: Int,
    val startDate: Long,
    val endDate: Long,
    val startTime: String,
    val endTime: String,
    val lastModified: Long,
    val isSynced: Boolean = false
) {

}
