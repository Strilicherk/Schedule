package com.example.schedule.feature_schedule.domain.use_case.appointment.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class SelectLocalAppointmentsOfTheYearUseCase (
    private val repository: AppointmentRepository,
) {
    operator fun invoke(fetchFromRemote: Boolean, startOfYear: Long, endOfYear: Long): Flow<Resource<List<Appointment>>> {
        return flow {
            emit(Resource.Loading())
            val localAppointments = repository.selectLocalAppointmentsOfTheYear(startOfYear, endOfYear)
            emit(Resource.Success(localAppointments))

            val isDbEmpty = localAppointments.isEmpty()
            val shouldJustGetFromApi = !isDbEmpty && !fetchFromRemote
            if (shouldJustGetFromApi) {
                emit(Resource.Loading(false))
                return@flow
            }

            try {
                emit(Resource.Loading())
            } catch (e: IOException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data"))
            }
        }
    }
}