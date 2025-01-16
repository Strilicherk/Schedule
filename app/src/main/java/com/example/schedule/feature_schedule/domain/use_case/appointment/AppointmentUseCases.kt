package com.example.schedule.feature_schedule.domain.use_case.appointment

import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.AddAppointmentToCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.ClearAppointmentCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.DeleteAppointmentFromCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetAppointmentFromCacheByIdUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetAppointmentsFromCacheByDateUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.UpdateAppointmentInCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.DeleteAppointmentFromRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.GetAllAppointmentsFromRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.AddAppointmentToRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.DeleteAppointmentFromRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetAppointmentsFromRemoteUseCase
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
    val updateAppointmentInCacheUseCase: UpdateAppointmentInCacheUseCase,
//    val loadAppointmentsFromRepositoryToCache: LoadAppointmentsFromRepositoryToCache,

    // local
    val deleteAppointmentFromRoomUseCase: DeleteAppointmentFromRoomUseCase,
    val getLastIdFromRoomUseCase: GetLastIdFromRoomUseCase,
    val getAllAppointmentsFromRoomUseCase: GetAllAppointmentsFromRoomUseCase,
    val addAppointmentToRoomUseCase: AddAppointmentToRoomUseCase,
    val upsertRemoteAppointmentsIntoRoomUseCase: UpsertRemoteAppointmentsIntoRoomUseCase,

    // remote
    val deleteAppointmentFromRemoteUseCase: DeleteAppointmentFromRemoteUseCase,
    val getAppointmentsFromRemoteUseCase: GetAppointmentsFromRemoteUseCase,
    val postRemoteAppointmentUseCase: PostUnsyncedAppointmentInRemoteUseCase,
)
