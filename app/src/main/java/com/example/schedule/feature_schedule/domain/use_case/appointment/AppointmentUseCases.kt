package com.example.schedule.feature_schedule.domain.use_case.appointment

import com.example.schedule.feature_schedule.domain.use_case.appointment.local.DeleteLocalAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.InsertLocalAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.SelectLocalAppointmentsOfTheYearUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpsertRemoteAppointmentsIntoRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.DeleteRemoteAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetRemoteAppointmentsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.PostUnsyncedAppointmentsUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class AppointmentUseCases @Inject constructor(
    val getRemoteAppointments: GetRemoteAppointmentsUseCase,
    val postRemoteAppointment: PostUnsyncedAppointmentsUseCase,
    val deleteRemoteAppointment: DeleteRemoteAppointmentUseCase,

    val selectLocalAppointmentsOfTheYearUseCase: SelectLocalAppointmentsOfTheYearUseCase,
    val insertLocalAppointmentUseCase: InsertLocalAppointmentUseCase,
    val deleteLocalAppointmentUseCase: DeleteLocalAppointmentUseCase,
    val updateLocalAppointments: UpsertRemoteAppointmentsIntoRoomUseCase,
)
