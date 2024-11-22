package com.example.schedule.di

import android.app.Application
import androidx.room.Room
import com.example.schedule.feature_schedule.data.data_source.local.AppointmentDatabase
import com.example.schedule.feature_schedule.data.data_source.remote.AppointmentApi
import com.example.schedule.feature_schedule.data.repository.AppointmentRepositoryImpl
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.AppointmentUseCases
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.DeleteLocalAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetRemoteAppointmentsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.InsertLocalAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.SelectLocalAppointmentsOfTheYearUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpsertRemoteAppointmentsIntoRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.DeleteRemoteAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.PostUnsyncedAppointmentsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.UpdateRemoteAppointmentUseCase
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
    fun provideScheduleRepository(api: AppointmentApi, db: AppointmentDatabase): AppointmentRepository {
        return AppointmentRepositoryImpl(api, db)
    }

    @Provides
    @Singleton
    fun providesAppointmentUseCases(repository: AppointmentRepository): AppointmentUseCases {
       return AppointmentUseCases(
           getRemoteAppointments = GetRemoteAppointmentsUseCase(PostUnsyncedAppointmentsUseCase(repository), UpsertRemoteAppointmentsIntoRoomUseCase(repository), repository),
           postRemoteAppointment = PostUnsyncedAppointmentsUseCase(repository),
           deleteRemoteAppointment = DeleteRemoteAppointmentUseCase(repository),
           updateRemoteAppointments = UpdateRemoteAppointmentUseCase(repository),

           selectLocalAppointmentsOfTheYearUseCase = SelectLocalAppointmentsOfTheYearUseCase(repository),
           selectUnsycedLocalAppointmentsUseCase = SelectUnsycedLocalAppointmentsUseCase(repository),
           insertLocalAppointmentUseCase = InsertLocalAppointmentUseCase(repository),
           deleteLocalAppointmentUseCase = DeleteLocalAppointmentUseCase(repository),
           updateLocalAppointments = UpsertRemoteAppointmentsIntoRoomUseCase(repository),
       )
    }
}