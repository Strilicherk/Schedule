package com.example.schedule.feature_schedule.domain.use_case.appointment

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostUnsyncedAppointmentInRemoteUseCase @Inject constructor(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(): Resource<Map<Int, Boolean>> {
        val syncedStatus = mutableMapOf<Int, Boolean>()
        val unsyncedLocalAppointments = repository.getUnsyncedAppointmentsFromRoom()

        if (unsyncedLocalAppointments.data.isNullOrEmpty()) {
            return Resource.Success(emptyMap())
        }

        unsyncedLocalAppointments.data.forEach { unsyncedAppointment ->
            val response = repository.addAppointmentToRemote(unsyncedAppointment)
            syncedStatus[unsyncedAppointment.id] = response is Resource.Success
        }

        return Resource.Success(syncedStatus)
    }
}
