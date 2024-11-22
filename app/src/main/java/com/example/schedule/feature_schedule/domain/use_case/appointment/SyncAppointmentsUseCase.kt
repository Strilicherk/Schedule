package com.example.schedule.feature_schedule.domain.use_case.appointment

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpsertRemoteAppointmentsIntoRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetRemoteAppointmentsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.PostUnsyncedAppointmentsUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SyncAppointmentsUseCase @Inject constructor(
    private val postUnsyncedAppointmentsUseCase: PostUnsyncedAppointmentsUseCase,
    private val getRemoteAppointmentsUseCase: GetRemoteAppointmentsUseCase,
    private val upsertRemoteAppointmentsIntoRoomUseCase: UpsertRemoteAppointmentsIntoRoomUseCase
) {
    operator suspend fun invoke() {
        val isDataAlreadySync = postUnsyncedAppointmentsUseCase.invoke()
        val remoteAppointmentList = getRemoteAppointmentsUseCase.invoke()
        val isAppSync = upsertRemoteAppointmentsIntoRoomUseCase.invoke(remoteAppointmentList)
    }
}