package com.example.schedule.feature_schedule.domain.use_case.appointment.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SelectAppointmentsFromRoomByYearUseCase @Inject constructor(
    private val repository: AppointmentRepository,
) {
    operator fun invoke(startOfYear: Long, endOfYear: Long): Flow<Resource<List<Appointment>>> {
        return flow {
            emit(Resource.Loading())

            if (startOfYear > endOfYear) emit(Resource.Error("Start of the year date cannot be older than end of the year date."))

            try {
                val localAppointments = repository.selectLocalAppointmentsOfTheYear(startOfYear, endOfYear)
                emit(Resource.Success(localAppointments))
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("IO Exception: ${e.message}"))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error("Exception: ${e.message}"))
            }
        }
    }
}