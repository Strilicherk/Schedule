package com.example.schedule.feature_schedule.domain.use_case.appointment

import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.AddAppointmentToCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.ClearAppointmentCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.DeleteAppointmentFromCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetAppointmentFromCacheByIdUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetAppointmentsFromCacheByDateUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetDatesFromCacheByAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.UpdateAppointmentInCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.DeleteAppointmentFromRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.GetAllAppointmentsFromRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.AddAppointmentToRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.ClearRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpdateAppointmentInRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.AddAppointmentToRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.DeleteAppointmentFromRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetAppointmentsFromRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.UpdateAppointmentInRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.sync.DownloadAppointmentsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.sync.ProcessDeletionsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.sync.UploadAppointmentsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.sync.UpsertUnsyncedAppointmentsToRemoteUseCase
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
    val getDatesFromCacheByAppointmentUseCase: GetDatesFromCacheByAppointmentUseCase,
    val updateAppointmentInCacheUseCase: UpdateAppointmentInCacheUseCase,

    // local
    val addAppointmentToRoomUseCase: AddAppointmentToRoomUseCase,
    val clearRoomUseCase: ClearRoomUseCase,
    val deleteAppointmentFromRoomUseCase: DeleteAppointmentFromRoomUseCase,
    val getAllAppointmentsFromRoomUseCase: GetAllAppointmentsFromRoomUseCase,
    val updateAppointmentInRoomUseCase: UpdateAppointmentInRoomUseCase,

    // remote
    val addAppointmentToRemoteUseCase: AddAppointmentToRemoteUseCase,
    val deleteAppointmentFromRemoteUseCase: DeleteAppointmentFromRemoteUseCase,
    val getAppointmentsFromRemoteUseCase: GetAppointmentsFromRemoteUseCase,
    val updateAppointmentInRemoteUseCase: UpdateAppointmentInRemoteUseCase,

    // sync
    val downloadAppointmentsUseCase: DownloadAppointmentsUseCase,
    val processDeletionsUseCase: ProcessDeletionsUseCase,
    val uploadAppointmentsUseCase: UploadAppointmentsUseCase,
    val upsertUnsyncedAppointmentsToRemoteUseCase: UpsertUnsyncedAppointmentsToRemoteUseCase,

    //general
    val validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase
)
