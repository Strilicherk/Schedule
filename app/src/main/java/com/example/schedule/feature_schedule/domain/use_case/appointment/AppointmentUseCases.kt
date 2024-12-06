package com.example.schedule.feature_schedule.domain.use_case.appointment

import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.AddAppointmentToCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.ClearAppointmentCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetAllCachedAppointmentsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetLastIdInCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.LoadAppointmentsFromRepositoryToCache
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.UpdateCachedAppointmentUseCase
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
    val getAllCachedAppointmentsUseCase: GetAllCachedAppointmentsUseCase,
    val getLastIdInCacheUseCase: GetLastIdInCacheUseCase,
    val loadAppointmentsFromRepositoryToCache: LoadAppointmentsFromRepositoryToCache,
    val saveLastIncAppointmentUseCase: UpsertLocalAppointmentUseCase,
    val updateCachedAppointmentUseCase: UpdateCachedAppointmentUseCase,

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
