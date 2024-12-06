package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import androidx.compose.ui.graphics.vector.VectorProperty
import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.ValidateAppointmentInfosUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddAppointmentToCacheUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase
) {
    suspend operator fun invoke(appointment: Appointment): Resource<Boolean> {
        val appointmentToCreate = validateAppointmentInfosUseCase.invoke(appointment).data

        try {
            repository.addAppointmentToCache(appointmentToCreate!!)
            withContext(Dispatchers.IO) {
                repository.upsertLocalAppointment(appointmentToCreate)
            }
            return Resource.Success(true)
        } catch (e: Exception) {
            return Resource.Error("Exception: ${e.message}")
        } catch (e: IOException) {
            return Resource.Error("IO Exception: ${e.message}")
        }
    }
}