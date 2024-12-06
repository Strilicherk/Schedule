package com.example.schedule.feature_schedule.data.mapper

import com.example.schedule.feature_schedule.data.data_source.local.AppointmentConverters
import com.example.schedule.feature_schedule.data.data_source.local.AppointmentEntity
import com.example.schedule.feature_schedule.data.data_source.remote.AppointmentDto
import com.example.schedule.feature_schedule.data.mapper.AppointmentMapper.dtoToEntity
import com.example.schedule.feature_schedule.domain.model.Appointment
import java.time.LocalTime

object AppointmentMapper {
    val converter = AppointmentConverters()

    fun AppointmentDto.dtoToEntity(): AppointmentEntity {
        return AppointmentEntity(
            id = this.id,
            title = this.title,
            notes = this.notes,
            color = this.color,
            startDate = this.startDate,
            endDate = this.endDate,
            startTime = this.startTime,
            endTime = this.endTime,
            lastModified = this.lastModified,
            isSynced = this.isSynced,
            hasBeenSynced = this.hasBeenSynced
        )
    }

    fun AppointmentEntity.entityToDto(): AppointmentDto {
        return AppointmentDto(
            id = this.id,
            title = this.title,
            notes = this.notes,
            color = this.color,
            startDate = this.startDate,
            endDate = this.endDate,
            startTime = this.startTime,
            endTime = this.endTime,
            lastModified = this.lastModified,
            isSynced = this.isSynced,
            hasBeenSynced = this.hasBeenSynced
        )
    }

    fun AppointmentEntity.entityToDomain(): Appointment {
        return Appointment(
            id = this.id,
            title = this.title,
            notes = this.notes,
            color = this.color,
            startDate = converter.fromTimestampWithoutTime(this.startDate),
            endDate = converter.fromTimestampWithoutTime(this.endDate),
            startTime = LocalTime.parse(this.startTime),
            endTime = LocalTime.parse(this.endTime),
            lastModified = converter.fromTimestamp(this.lastModified),
            isSynced = this.isSynced,
            hasBeenSynced = this.hasBeenSynced
        )
    }

    fun Appointment.domainToEntity(): AppointmentEntity {
        return AppointmentEntity(
            id = this.id,
            title = this.title,
            notes = this.notes,
            color = this.color,
            startDate = converter.toTimestampWithoutTime(this.startDate),
            endDate = converter.toTimestampWithoutTime(this.endDate),
            startTime = this.startTime.toString(),
            endTime = this.endTime.toString(),
            lastModified = converter.toTimestamp(this.lastModified),
            isSynced = this.isSynced,
            hasBeenSynced = this.hasBeenSynced
        )
    }
}


