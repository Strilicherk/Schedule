package com.example.schedule.feature_schedule.data.repository

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.data.data_source.local.AppointmentDatabase
import com.example.schedule.feature_schedule.data.data_source.remote.AppointmentApi
import com.example.schedule.feature_schedule.data.mapper.toDomainModel
import com.example.schedule.feature_schedule.data.mapper.toEntity
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentRepositoryImpl @Inject constructor(
    val api: AppointmentApi,
    val db: AppointmentDatabase
) : AppointmentRepository {
    private val dao = db.dao

    override suspend fun syncRemoteAppointments(): List<Appointment> {
        return api.getAppointments().map { it.toEntity().toDomainModel()}
    }

    override suspend fun postUnsyncedRemoteAppointments(appointmentList: List<Appointment>): Response {
        TODO("Not yet implemented")
    }

    override suspend fun updateRemoteAppointments(appointmentList: List<Appointment>): Response {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRemoteAppointments(appointmentList: List<Appointment>): Response {
        TODO("Not yet implemented")
    }

    override suspend fun selectAppointments(): List<Appointment> {
        return dao.selectAppointments().map { it.toDomainModel() }
    }

    override suspend fun selectLocalAppointmentsOfTheYear(): List<Appointment> {
        return dao.selectAppointmentsOfTheYear().map { it.toDomainModel() }
    }


    override suspend fun deleteLocalAppointment(appointment: Appointment) {
        TODO("Not yet implemented")
    }

    override suspend fun selectUnsyncedLocalAppointments(): List<Appointment> {
        TODO("Not yet implemented")
    }

    override suspend fun upsertLocalAppointment(appointment: Appointment) {
        TODO("Not yet implemented")
    }

}


//class AppointmentRemoteRepositoryImpl @Inject constructor(
//    private val api: AppointmentApi,
//) : AppointmentRemoteRepository {
//    operator fun invoke(): Flow<Resource<List<Appointment>>> = flow{
//        try {
//            emit(Resource.Loading())
//            val appointments = getAppointments().first()
//            emit(Resource.Success(appointments))
//        } catch (e: HttpException) {
//            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred."))
//        } catch (e: IOException) {
//            emit(Resource.Error("Couldn't reach server. Please check your internet connection."))
//        }
//    }
//
//    override suspend fun getAppointments(): Flow<List<Appointment>> = flow {
//        val dtoList = api.getAppointments()
//        emit(dtoList.toAppointmentList())
//    }
//}

//class AppointmentLocalRepositoryImpl(
//    private val dao: AppointmentDao
//): AppointmentLocalRepository {
//    override fun getAppointments(): Flow<List<Appointment>> {
//        return dao.getAppointments()
//    }
//
//    override suspend fun insertOrUpdateAppointment(appointment: Appointment) {
//        return dao.insertOrUpdateAppointment(appointment)
//    }
//
//    override suspend fun deleteAppointment(appointment: Appointment) {
//        return dao.deleteAppointment(appointment)
//    }
//
//}