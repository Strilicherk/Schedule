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
import java.time.LocalDateTime

@Entity
data class Appointment(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val notes: String,
    val color: Int,
    val startDate: Long,
    val endDate: Long,
    val repeat: Boolean,
    val repeatOption: RepeatOptionsEnum,
) {
    companion object{
        val noteColors = listOf(Blue, Red, Green, Gray, Yellow, Black, Cyan)
    }
}
