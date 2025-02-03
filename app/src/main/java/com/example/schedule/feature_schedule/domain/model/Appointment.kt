package com.example.schedule.feature_schedule.domain.model

import com.example.schedule.ui.theme.CustomBlue
import com.example.schedule.ui.theme.CustomGreen
import com.example.schedule.ui.theme.CustomLightBlue
import com.example.schedule.ui.theme.CustomOrange
import com.example.schedule.ui.theme.CustomPurple
import com.example.schedule.ui.theme.CustomRed
import com.example.schedule.ui.theme.CustomWhite
import com.example.schedule.ui.theme.CustomYellow
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
        val noteColors = listOf(CustomBlue, CustomRed, CustomGreen, CustomOrange,
            CustomYellow, CustomWhite, CustomLightBlue, CustomPurple)
    }

    fun compareAppointmentDates(appointment: Appointment): Boolean {
        return this.startDate == appointment.startDate && this.endDate == appointment.endDate
    }
}
