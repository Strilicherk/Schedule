package com.example.schedule.feature_schedule.data.data_source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [AppointmentEntity::class],
    version = 1
)
@TypeConverters(AppointmentConverters::class)
abstract class AppointmentDatabase : RoomDatabase() {
    abstract val dao: AppointmentDao

    companion object {
        const val DATABASE_NAME = "schedule_db"
    }
}