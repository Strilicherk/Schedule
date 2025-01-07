package com.example.schedule.feature_schedule.domain.use_case.appointment

import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.AddAppointmentToCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.ClearAppointmentCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.DeleteAppointmentFromCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetAppointmentFromCacheByIdUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetAppointmentsFromCacheByDateUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.UpdateAppointmentFromCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.DeleteLocalAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.GetLastIdFromRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.SelectLocalAppointmentsOfTheYearUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpsertLocalAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpsertRemoteAppointmentsIntoRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.DeleteRemoteAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetRemoteAppointmentsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.PostUnsyncedAppointmentsUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
data class AppointmentUseCases @Inject constructor(
    // cache
    val addAppointmentToCacheUseCase: AddAppointmentToCacheUseCase,
    val clearAppointmentCacheUseCase: ClearAppointmentCacheUseCase,
    val deleteAppointmentFromCacheUseCase: DeleteAppointmentFromCacheUseCase,
    val getAppointmentFromCacheByIdUseCase: GetAppointmentFromCacheByIdUseCase,
    val getAppointmentsFromCacheByDateUseCase: GetAppointmentsFromCacheByDateUseCase,
    val updateAppointmentFromCacheUseCase: UpdateAppointmentFromCacheUseCase,
//    val loadAppointmentsFromRepositoryToCache: LoadAppointmentsFromRepositoryToCache,

    // local
    val deleteLocalAppointmentUseCase: DeleteLocalAppointmentUseCase,
    val getLastIdFromRoomUseCase: GetLastIdFromRoomUseCase,
    val selectLocalAppointmentsOfTheYearUseCase: SelectLocalAppointmentsOfTheYearUseCase,
    val upsertLocalAppointmentUseCase: UpsertLocalAppointmentUseCase,
    val upsertRemoteAppointmentsIntoRoomUseCase: UpsertRemoteAppointmentsIntoRoomUseCase,

    // remote
    val deleteRemoteAppointmentUseCase: DeleteRemoteAppointmentUseCase,
    val getRemoteAppointmentsUseCase: GetRemoteAppointmentsUseCase,
    val postRemoteAppointmentUseCase: PostUnsyncedAppointmentsUseCase,
)
