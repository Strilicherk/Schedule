package com.example.schedule.feature_schedule.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.schedule.feature_schedule.domain.model.Appointment

@Database(entities = [Appointment::class], version = 1)
@TypeConverters(ScheduleConverters::class)
abstract class ScheduleDatabase : RoomDatabase() {
    abstract val dao: ScheduleDao

    companion object {
        const val DATABASE_NAME = "schedule_db"
    }
}