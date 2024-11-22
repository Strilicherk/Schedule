package com.example.schedule.feature_schedule.domain.use_case.appointment

import com.example.schedule.feature_schedule.domain.use_case.appointment.local.DeleteLocalAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.InsertLocalAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.SelectLocalAppointmentsOfTheYearUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpsertRemoteAppointmentsIntoRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.DeleteRemoteAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetRemoteAppointmentsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.PostUnsyncedAppointmentsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.UpdateRemoteAppointmentUseCase

data class AppointmentUseCases(
    val getRemoteAppointments: GetRemoteAppointmentsUseCase,
    val postRemoteAppointment: PostUnsyncedAppointmentsUseCase,
    val deleteRemoteAppointment: DeleteRemoteAppointmentUseCase,
    val updateRemoteAppointments: UpdateRemoteAppointmentUseCase,

    val selectLocalAppointmentsOfTheYearUseCase: SelectLocalAppointmentsOfTheYearUseCase,
    val selectUnsycedLocalAppointmentsUseCase: SelectUnsycedLocalAppointmentsUseCase,
    val insertLocalAppointmentUseCase: InsertLocalAppointmentUseCase,
    val deleteLocalAppointmentUseCase: DeleteLocalAppointmentUseCase,
    val updateLocalAppointments: UpsertRemoteAppointmentsIntoRoomUseCase,
)
