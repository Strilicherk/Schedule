package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAppointmentsFromCacheByDateUseCase @Inject constructor(
    private val repository: AppointmentRepository
){

}