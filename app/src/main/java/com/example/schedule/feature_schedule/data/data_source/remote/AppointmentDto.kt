package com.example.schedule.feature_schedule.data.data_source.remote

import com.example.schedule.feature_schedule.domain.model.Appointment
import java.util.Date

data class AppointmentDto(
    val id: Int,
    val title: String,
    val notes: String,
    val color: Int,
    val startDate: Long,
    val endDate: Long,
    val startTime: String,
    val endTime: String,
    val lastModified: Long,
    val isSynced: Boolean,
    val hasBeenSynced: Boolean
)