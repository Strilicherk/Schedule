package com.example.schedule.feature_schedule.domain.use_case.appointment

import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpsertRemoteAppointmentsIntoRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetRemoteAppointmentsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.PostUnsyncedAppointmentsUseCase
import javax.inject.Inject

class SyncAppointmentsUseCase @Inject constructor(
    private val postUnsyncedAppointmentsUseCase: PostUnsyncedAppointmentsUseCase,
    private val getRemoteAppointmentsUseCase: GetRemoteAppointmentsUseCase,
    private val upsertRemoteAppointmentsIntoRoomUseCase: UpsertRemoteAppointmentsIntoRoomUseCase
) {
    operator suspend fun invoke() {
        val remoteAppointmentList = getRemoteAppointmentsUseCase.invoke()
        // delete
        // resolve os conflitos de ids antes de subir
        val isDataAlreadySync = postUnsyncedAppointmentsUseCase.invoke()
        val isAppSync = upsertRemoteAppointmentsIntoRoomUseCase.invoke(remoteAppointmentList)
    }
}