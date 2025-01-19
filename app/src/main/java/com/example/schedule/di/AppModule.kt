package com.example.schedule.di

import android.app.Application
import androidx.room.Room
import com.example.schedule.feature_schedule.data.data_source.cache.AppointmentCache
import com.example.schedule.feature_schedule.data.data_source.local.AppointmentDatabase
import com.example.schedule.feature_schedule.data.data_source.remote.AppointmentApi
import com.example.schedule.feature_schedule.data.repository.AppointmentRepositoryImpl
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.AppointmentUseCases
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.AddAppointmentToCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.ClearAppointmentCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.ValidateAppointmentInfosUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.DeleteAppointmentFromCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetAppointmentFromCacheByIdUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetAppointmentsFromCacheByDateUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetDatesFromCacheByAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.UpdateAppointmentInCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.DeleteAppointmentFromRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetAppointmentsFromRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.GetAllAppointmentsFromRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.AddAppointmentToRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.ClearRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpdateAppointmentInRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.AddAppointmentToRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.DeleteAppointmentFromRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.UpdateAppointmentInRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.sync.DownloadAppointmentsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.sync.ProcessDeletionsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.sync.UploadAppointmentsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.sync.UpsertUnsyncedAppointmentsToRemoteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import kotlin.math.log

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppointmentDatabase {
        return Room.databaseBuilder(
            app,
            AppointmentDatabase::class.java,
            AppointmentDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideApi(): AppointmentApi {
        return Retrofit.Builder()
            .baseUrl(AppointmentApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AppointmentApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCache(): AppointmentCache {
        return AppointmentCache()
    }

    @Provides
    @Singleton
    fun provideRepository(
        api: AppointmentApi,
        db: AppointmentDatabase,
        cache: AppointmentCache
    ): AppointmentRepository {
        return AppointmentRepositoryImpl(api, db, cache)
    }

    @Provides
    @Singleton
    fun provideLogger(): Logger {
        return LoggerFactory.getLogger("ApplicationLogger")
    }

    @Provides
    @Singleton
    fun provideValidateAppointmentInfosUseCase(
        repository: AppointmentRepository,
        logger: Logger
    ): ValidateAppointmentInfosUseCase {
        return ValidateAppointmentInfosUseCase(repository, logger)
    }

    //cache
    @Provides
    @Singleton
    fun provideGetAppointmentFromCacheByIdUseCase(
        repository: AppointmentRepository,
        logger: Logger
    ): GetAppointmentFromCacheByIdUseCase {
        return GetAppointmentFromCacheByIdUseCase(repository, logger)
    }

    @Provides
    @Singleton
    fun provideGetAppointmentsFromCacheByDateUseCase(
        repository: AppointmentRepository,
        logger: Logger
    ): GetAppointmentsFromCacheByDateUseCase {
        return GetAppointmentsFromCacheByDateUseCase(repository, logger)
    }

    @Provides
    @Singleton
    fun provideAddAppointmentToCacheUseCase(
        repository: AppointmentRepository,
        validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase,
        addAppointmentToRoomUseCase: AddAppointmentToRoomUseCase,
        logger: Logger
    ): AddAppointmentToCacheUseCase {
        return AddAppointmentToCacheUseCase(
            repository,
            validateAppointmentInfosUseCase,
            addAppointmentToRoomUseCase,
            logger
        )
    }

    @Provides
    @Singleton
    fun provideClearAppointmentCacheUseCase(
        repository: AppointmentRepository,
        logger: Logger
    ): ClearAppointmentCacheUseCase {
        return ClearAppointmentCacheUseCase(repository, logger)
    }

    @Provides
    @Singleton
    fun provideDeleteAppointmentFromCacheUseCase(
        repository: AppointmentRepository,
        deleteAppointmentFromRoomUseCase: DeleteAppointmentFromRoomUseCase,
        addAppointmentToCacheUseCase: AddAppointmentToCacheUseCase,
        logger: Logger
    ): DeleteAppointmentFromCacheUseCase {
        return DeleteAppointmentFromCacheUseCase(
            repository,
            deleteAppointmentFromRoomUseCase,
            addAppointmentToCacheUseCase,
            logger
        )
    }

    @Provides
    @Singleton
    fun provideUpdateAppointmentInCacheUseCase(
        repository: AppointmentRepository,
        addAppointmentToCacheUseCase: AddAppointmentToCacheUseCase,
        updateAppointmentInRoomUseCase: UpdateAppointmentInRoomUseCase,
        getDatesFromCacheByAppointmentUseCase: GetDatesFromCacheByAppointmentUseCase,
        getAppointmentFromCacheByIdUseCase: GetAppointmentFromCacheByIdUseCase,
        validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase,
        logger: Logger
    ): UpdateAppointmentInCacheUseCase {
        return UpdateAppointmentInCacheUseCase(
            repository,
            addAppointmentToCacheUseCase,
            updateAppointmentInRoomUseCase,
            getDatesFromCacheByAppointmentUseCase,
            getAppointmentFromCacheByIdUseCase,
            validateAppointmentInfosUseCase,
            logger
        )
    }

    //room
    @Provides
    @Singleton
    fun provideAddAppointmentToRoomUseCase(
        repository: AppointmentRepository,
        validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase,
        logger: Logger
    ): AddAppointmentToRoomUseCase {
        return AddAppointmentToRoomUseCase(repository, validateAppointmentInfosUseCase, logger)
    }

    @Provides
    @Singleton
    fun provideDeleteAppointmentFromRoomUseCase(
        repository: AppointmentRepository,
        logger: Logger
    ): DeleteAppointmentFromRoomUseCase {
        return DeleteAppointmentFromRoomUseCase(repository, logger)
    }

    @Provides
    @Singleton
    fun provideGetAllAppointmentsFromRoomUseCase(
        repository: AppointmentRepository,
        logger: Logger
    ): GetAllAppointmentsFromRoomUseCase {
        return GetAllAppointmentsFromRoomUseCase(repository, logger)
    }

    //remote
    @Provides
    @Singleton
    fun providesGetAppointmentsFromRemoteUseCase(
        repository: AppointmentRepository,
        logger: Logger
    ): GetAppointmentsFromRemoteUseCase {
        return GetAppointmentsFromRemoteUseCase(repository, logger)
    }

    @Provides
    @Singleton
    fun provideUpdateAppointmentInRemoteUseCase(
        repository: AppointmentRepository,
        logger: Logger
    ): UpdateAppointmentInRemoteUseCase {
        return UpdateAppointmentInRemoteUseCase(repository, logger)
    }

    @Provides
    @Singleton
    fun provideAddAppointmentToRemoteUseCase(
        repository: AppointmentRepository,
        logger: Logger
    ): AddAppointmentToRemoteUseCase {
        return AddAppointmentToRemoteUseCase(repository, logger)
    }

    @Provides
    @Singleton
    fun providesDeleteAppointmentFromRemoteUseCase(
        repository: AppointmentRepository,
        logger: Logger
    ): DeleteAppointmentFromRemoteUseCase {
        return DeleteAppointmentFromRemoteUseCase(repository, logger)
    }

    //sync
    @Provides
    @Singleton
    fun provideUpsertUnsyncedAppointmentsToRemoteUseCase(
        updateAppointmentInRemoteUseCase: UpdateAppointmentInRemoteUseCase,
        updateAppointmentInRoomUseCase: UpdateAppointmentInRoomUseCase,
        addAppointmentToRemoteUseCase: AddAppointmentToRemoteUseCase,
        logger: Logger
    ): UpsertUnsyncedAppointmentsToRemoteUseCase {
        return UpsertUnsyncedAppointmentsToRemoteUseCase(
            updateAppointmentInRemoteUseCase,
            updateAppointmentInRoomUseCase,
            addAppointmentToRemoteUseCase,
            logger
        )
    }

    @Provides
    @Singleton
    fun provideProcessDeletionsUseCase(
        repository: AppointmentRepository,
        deleteAppointmentFromRemoteUseCase: DeleteAppointmentFromRemoteUseCase,
        logger: Logger
    ): ProcessDeletionsUseCase {
        return ProcessDeletionsUseCase(repository, deleteAppointmentFromRemoteUseCase, logger)
    }

    @Provides
    @Singleton
    fun provideDownloadAppointmentsUseCase(
        getAppointmentsFromRemoteUseCase: GetAppointmentsFromRemoteUseCase,
        clearRoomUseCase: ClearRoomUseCase,
        addAppointmentToCacheUseCase: AddAppointmentToCacheUseCase,
        logger: Logger
    ): DownloadAppointmentsUseCase {
        return DownloadAppointmentsUseCase(
            getAppointmentsFromRemoteUseCase,
            clearRoomUseCase,
            addAppointmentToCacheUseCase,
            logger
        )
    }

    @Provides
    @Singleton
    fun provideUploadAppointmentsUseCase(
        repository: AppointmentRepository,
        getAppointmentsFromRemoteUseCase: GetAppointmentsFromRemoteUseCase,
        upsertUnsyncedAppointmentsToRemoteUseCase: UpsertUnsyncedAppointmentsToRemoteUseCase,
        processDeletionsUseCase: ProcessDeletionsUseCase,
        logger: Logger
    ): UploadAppointmentsUseCase {
        return UploadAppointmentsUseCase(
            repository,
            getAppointmentsFromRemoteUseCase,
            upsertUnsyncedAppointmentsToRemoteUseCase,
            processDeletionsUseCase,
            logger
        )
    }

    @Provides
    @Singleton
    fun provideAppointmentUseCases(
        //general
        validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase,

        //cache
        addAppointmentToCacheUseCase: AddAppointmentToCacheUseCase,
        updateAppointmentInCacheUseCase: UpdateAppointmentInCacheUseCase,
        deleteAppointmentFromCacheUseCase: DeleteAppointmentFromCacheUseCase,
        clearAppointmentCacheUseCase: ClearAppointmentCacheUseCase,
        getAppointmentFromCacheByIdUseCase: GetAppointmentFromCacheByIdUseCase,
        getAppointmentsFromCacheByDateUseCase: GetAppointmentsFromCacheByDateUseCase,
        getDatesFromCacheByAppointmentUseCase: GetDatesFromCacheByAppointmentUseCase,

        //local
        addAppointmentToRoomUseCase: AddAppointmentToRoomUseCase,
        updateAppointmentInRoomUseCase: UpdateAppointmentInRoomUseCase,
        deleteAppointmentFromRoomUseCase: DeleteAppointmentFromRoomUseCase,
        getAllAppointmentsFromRoomUseCase: GetAllAppointmentsFromRoomUseCase,
        clearRoomUseCase: ClearRoomUseCase,

        //remote
        addAppointmentToRemoteUseCase: AddAppointmentToRemoteUseCase,
        deleteAppointmentFromRemoteUseCase: DeleteAppointmentFromRemoteUseCase,
        getAppointmentsFromRemoteUseCase: GetAppointmentsFromRemoteUseCase,
        updateAppointmentInRemoteUseCase: UpdateAppointmentInRemoteUseCase,

        //sync
        upsertUnsyncedAppointmentsToRemoteUseCase: UpsertUnsyncedAppointmentsToRemoteUseCase,
        processDeletionsUseCase: ProcessDeletionsUseCase,
        downloadAppointmentsUseCase: DownloadAppointmentsUseCase,
        uploadAppointmentsUseCase: UploadAppointmentsUseCase
    ): AppointmentUseCases {

        return AppointmentUseCases(
            // cache
            addAppointmentToCacheUseCase = addAppointmentToCacheUseCase,
            clearAppointmentCacheUseCase = clearAppointmentCacheUseCase,
            deleteAppointmentFromCacheUseCase = deleteAppointmentFromCacheUseCase,
            getAppointmentFromCacheByIdUseCase = getAppointmentFromCacheByIdUseCase,
            getAppointmentsFromCacheByDateUseCase = getAppointmentsFromCacheByDateUseCase,
            getDatesFromCacheByAppointmentUseCase = getDatesFromCacheByAppointmentUseCase,
            updateAppointmentInCacheUseCase = updateAppointmentInCacheUseCase,

            // local
            addAppointmentToRoomUseCase = addAppointmentToRoomUseCase,
            clearRoomUseCase = clearRoomUseCase,
            deleteAppointmentFromRoomUseCase = deleteAppointmentFromRoomUseCase,
            getAllAppointmentsFromRoomUseCase = getAllAppointmentsFromRoomUseCase,
            updateAppointmentInRoomUseCase = updateAppointmentInRoomUseCase,

            // remote
            addAppointmentToRemoteUseCase = addAppointmentToRemoteUseCase,
            deleteAppointmentFromRemoteUseCase = deleteAppointmentFromRemoteUseCase,
            getAppointmentsFromRemoteUseCase = getAppointmentsFromRemoteUseCase,
            updateAppointmentInRemoteUseCase = updateAppointmentInRemoteUseCase,

            // sync
            downloadAppointmentsUseCase = downloadAppointmentsUseCase,
            processDeletionsUseCase = processDeletionsUseCase,
            uploadAppointmentsUseCase = uploadAppointmentsUseCase,
            upsertUnsyncedAppointmentsToRemoteUseCase = upsertUnsyncedAppointmentsToRemoteUseCase,

            //general
            validateAppointmentInfosUseCase = validateAppointmentInfosUseCase
        )
    }
}