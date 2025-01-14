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
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetRemoteAppointmentsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.GetAllAppointmentsFromRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.AddAppointmentToRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpdateAppointmentInRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.UpsertRemoteAppointmentsIntoRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.DeleteRemoteAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.PostUnsyncedAppointmentsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideScheduleDatabase(app: Application): AppointmentDatabase {
        return Room.databaseBuilder(
            app,
            AppointmentDatabase::class.java,
            AppointmentDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideScheduleApi(): AppointmentApi {
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
    fun provideScheduleRepository(
        api: AppointmentApi,
        db: AppointmentDatabase,
        cache: AppointmentCache
    ): AppointmentRepository {
        return AppointmentRepositoryImpl(api, db, cache)
    }

    @Provides
    @Singleton
    fun providesLogger(): Logger {
        return LoggerFactory.getLogger("ApplicationLogger")
    }

    @Provides
    @Singleton
    fun providesValidateAppointmentInfosUseCase(repository: AppointmentRepository, logger: Logger): ValidateAppointmentInfosUseCase {
        return ValidateAppointmentInfosUseCase(repository, logger)
    }

    @Provides
    @Singleton
    fun providesAddAppointmentToRoomUseCase(
        repository: AppointmentRepository,
        validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase,
        logger: Logger
    ): AddAppointmentToRoomUseCase {
        return AddAppointmentToRoomUseCase(repository, validateAppointmentInfosUseCase, logger)
    }

    @Provides
    @Singleton
    fun providesDeleteAppointmentFromRoomUseCase(repository: AppointmentRepository): DeleteAppointmentFromRoomUseCase {
        return DeleteAppointmentFromRoomUseCase(repository)
    }

    @Provides
    @Singleton
    fun providesAppointmentUseCases(
        repository: AppointmentRepository,
        logger: Logger,
        addAppointmentToCacheUseCase: AddAppointmentToCacheUseCase,
        getDatesFromCacheByAppointmentUseCase: GetDatesFromCacheByAppointmentUseCase,
        getAppointmentFromCacheByIdUseCase: GetAppointmentFromCacheByIdUseCase,

        addAppointmentToRoomUseCase: AddAppointmentToRoomUseCase,
        updateAppointmentInRoomUseCase: UpdateAppointmentInRoomUseCase,
        deleteAppointmentFromRoomUseCase: DeleteAppointmentFromRoomUseCase,

        validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase,
    ): AppointmentUseCases {

        return AppointmentUseCases(
            // cache
            addAppointmentToCacheUseCase = AddAppointmentToCacheUseCase(
                repository, validateAppointmentInfosUseCase, addAppointmentToRoomUseCase, logger),
            clearAppointmentCacheUseCase = ClearAppointmentCacheUseCase(
                repository, logger),
            deleteAppointmentFromCacheUseCase = DeleteAppointmentFromCacheUseCase(
                repository, deleteAppointmentFromRoomUseCase, addAppointmentToCacheUseCase, logger),
            getAppointmentFromCacheByIdUseCase = GetAppointmentFromCacheByIdUseCase(
                repository, logger),
            getAppointmentsFromCacheByDateUseCase = GetAppointmentsFromCacheByDateUseCase(
                repository, logger),
            updateAppointmentInCacheUseCase = UpdateAppointmentInCacheUseCase(
                repository, updateAppointmentInRoomUseCase, getDatesFromCacheByAppointmentUseCase,
                getAppointmentFromCacheByIdUseCase, validateAppointmentInfosUseCase, logger),

            // local
            deleteAppointmentFromRoomUseCase = deleteAppointmentFromRoomUseCase,
            getLastIdFromRoomUseCase = GetLastIdFromRoomUseCase(repository),
            getAllAppointmentsFromRoomUseCase = GetAllAppointmentsFromRoomUseCase(repository),
            addAppointmentToRoomUseCase = addAppointmentToRoomUseCase,
            upsertRemoteAppointmentsIntoRoomUseCase = UpsertRemoteAppointmentsIntoRoomUseCase(repository),

            // remote
            deleteRemoteAppointmentUseCase = DeleteRemoteAppointmentUseCase(repository),
            getRemoteAppointmentsUseCase = GetRemoteAppointmentsUseCase(repository),
            postRemoteAppointmentUseCase = PostUnsyncedAppointmentsUseCase(repository)
        )
    }
}