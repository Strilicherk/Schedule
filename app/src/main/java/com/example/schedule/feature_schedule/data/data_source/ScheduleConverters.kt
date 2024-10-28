package com.example.schedule.feature_schedule.data.data_source

import android.graphics.Color
import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScheduleConverters {

    private val formatter = DateTimeFormatter.ISO_DATE_TIME

    @TypeConverter
    fun fromLocalDateTime(value: LocalDateTime?): String? {
        return value?.format(formatter)
    }

    @TypeConverter
    fun toLocalDateTime(value: String?): LocalDateTime? {
        return value?.let{
            LocalDateTime.parse(it, formatter)
        }
    }
}