package com.example.schedule.di

import android.app.Application
import androidx.room.Room
import com.example.schedule.feature_schedule.data.data_source.ScheduleDatabase
import com.example.schedule.feature_schedule.data.repository.ScheduleRepositoryImpl
import com.example.schedule.feature_schedule.domain.repository.ScheduleRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.AppointmentUseCases
import com.example.schedule.feature_schedule.domain.use_case.appointment.DeleteAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.GetAppointmentsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.InsertOrUpdateAppointmentUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideScheduleDatabase(app: Application): ScheduleDatabase {
        return Room.databaseBuilder(
            app,
            ScheduleDatabase::class.java,
            ScheduleDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideScheduleRepository(db: ScheduleDatabase): ScheduleRepository {
        return ScheduleRepositoryImpl(db.dao)
    }

    @Provides
    @Singleton
    fun providesAppointmentUseCases(repository: ScheduleRepository): AppointmentUseCases {
       return AppointmentUseCases(
           insertOrUpdateAppointment = InsertOrUpdateAppointmentUseCase(repository),
           getAppointments = GetAppointmentsUseCase(repository),
           deleteAppointment = DeleteAppointmentUseCase(repository)
       )
    }
}