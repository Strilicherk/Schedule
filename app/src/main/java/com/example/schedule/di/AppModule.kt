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
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetAllCachedAppointmentsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetLastIdInCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.LoadAppointmentsFromRepositoryToCache
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.SaveLastIdInCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.UpdateCachedAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.DeleteLocalAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.GetLastIdFromRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetRemoteAppointmentsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.SelectLocalAppointmentsOfTheYearUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpsertLocalAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpsertRemoteAppointmentsIntoRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.DeleteRemoteAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.PostUnsyncedAppointmentsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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
    fun provideScheduleRepository(
        api: AppointmentApi,
        db: AppointmentDatabase,
        cache: AppointmentCache
    ): AppointmentRepository {
        return AppointmentRepositoryImpl(api, db, cache)
    }

    @Provides
    @Singleton
    fun provideCache(repository: AppointmentRepository): AppointmentCache {
        return AppointmentCache(repository)
    }

    @Provides
    @Singleton
    fun providesAppointmentUseCases(repository: AppointmentRepository): AppointmentUseCases {
        return AppointmentUseCases(
            // cache
            addAppointmentToCacheUseCase = AddAppointmentToCacheUseCase(),
            clearAppointmentCacheUseCase = ClearAppointmentCacheUseCase(),
            getAllCachedAppointmentsUseCase = GetAllCachedAppointmentsUseCase(),
            getLastIdInCacheUseCase = GetLastIdInCacheUseCase(repository, GetLastIdFromRoomUseCase(repository), SaveLastIdInCacheUseCase(repository)),
            loadAppointmentsFromRepositoryToCache = LoadAppointmentsFromRepositoryToCache(),
            saveLastIncAppointmentUseCase = UpsertLocalAppointmentUseCase(repository),
            updateCachedAppointmentUseCase = UpdateCachedAppointmentUseCase(),

            // local
            deleteLocalAppointmentUseCase = DeleteLocalAppointmentUseCase(repository),
            getLastIdFromRoomUseCase = GetLastIdFromRoomUseCase(repository),
            selectLocalAppointmentsOfTheYearUseCase = SelectLocalAppointmentsOfTheYearUseCase(repository),
            upsertLocalAppointmentUseCase = UpsertLocalAppointmentUseCase(repository),
            upsertRemoteAppointmentsIntoRoomUseCase = UpsertRemoteAppointmentsIntoRoomUseCase(repository),

            // remote
            deleteRemoteAppointmentUseCase = DeleteRemoteAppointmentUseCase(repository),
            getRemoteAppointmentsUseCase = GetRemoteAppointmentsUseCase(repository),
            postRemoteAppointmentUseCase = PostUnsyncedAppointmentsUseCase(repository),
        )
    }
}