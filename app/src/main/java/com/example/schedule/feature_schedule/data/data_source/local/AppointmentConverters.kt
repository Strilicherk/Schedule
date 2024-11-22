package com.example.schedule.feature_schedule.data.data_source.local

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

class AppointmentConverters {
    @TypeConverter
    fun fromTimestamp(date: Long): LocalDateTime {
        return date.let {
            Instant.ofEpochMilli(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
        }
    }

    @TypeConverter
    fun toTimestamp(date: LocalDateTime): Long {
        return date.toInstant(ZoneOffset.UTC).toEpochMilli()
    }

    @TypeConverter
    fun fromTimestampWithoutTime(date: Long): LocalDate {
        return date.let {
            Instant.ofEpochMilli(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }
    }

    @TypeConverter
    fun toTimestampWithoutTime(date: LocalDate): Long {
        return date.atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }
}