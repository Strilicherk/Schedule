package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import androidx.compose.ui.graphics.vector.VectorProperty
import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.ValidateAppointmentInfosUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpsertLocalAppointmentUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddAppointmentToCacheUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase,
    private val upsertLocalAppointmentUseCase: UpsertLocalAppointmentUseCase
) {
    suspend operator fun invoke(appointment: Appointment): Resource<Boolean> {
        val appointmentToCreate = validateAppointmentInfosUseCase.invoke(appointment).data
            ?: throw Exception("ValidateAppointment returns null")

        try {
            if (repository.addAppointmentToCache(appointmentToCreate).data!!) {
                val createAppointmentInRoom = withContext(Dispatchers.IO) {
                    val res = upsertLocalAppointmentUseCase.invoke(appointmentToCreate)
                    res.first().data
                }

                return if (createAppointmentInRoom == true) Resource.Success(true) else Resource.Error(
                    "Failed to create appointment in Room."
                )
            }
            return Resource.Error("Failed to add appointment to cache.")
        } catch (e: Exception) {
            return Resource.Error("Exception: ${e.message}")
        } catch (e: IOException) {
            return Resource.Error("IO Exception: ${e.message}")
        }
    }
}