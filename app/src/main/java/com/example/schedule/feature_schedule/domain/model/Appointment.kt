package com.example.schedule.feature_schedule.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Cyan
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.schedule.feature_schedule.data.data_source.remote.AppointmentDto
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class Appointment(
    val id: Int,
    val title: String,
    val notes: String,
    val color: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val lastModified: LocalDateTime = LocalDateTime.now(),
    val isSynced: Boolean = false,
    val hasBeenSynced: Boolean = false
//    val repeat: Boolean,
//    val repeatOption: RepeatOptionsEnum,
) {
    companion object{
        val noteColors = listOf(Blue, Red, Green, Gray, Yellow, Black, Cyan)
    }
}
