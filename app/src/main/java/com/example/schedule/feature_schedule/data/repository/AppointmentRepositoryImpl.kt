package com.example.schedule.feature_schedule.data.repository

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.data.data_source.cache.AppointmentCache
import com.example.schedule.feature_schedule.data.data_source.local.AppointmentDatabase
import com.example.schedule.feature_schedule.data.data_source.remote.AppointmentApi
import com.example.schedule.feature_schedule.data.mapper.AppointmentMapper.domainToEntity
import com.example.schedule.feature_schedule.data.mapper.AppointmentMapper.dtoToEntity
import com.example.schedule.feature_schedule.data.mapper.AppointmentMapper.entityToDomain
import com.example.schedule.feature_schedule.data.mapper.AppointmentMapper.entityToDto
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentRepositoryImpl @Inject constructor(
    private val api: AppointmentApi,
    private val db: AppointmentDatabase,
    private val cache: AppointmentCache
) : AppointmentRepository {
    private val dao = db.dao

    // local
    override suspend fun selectAppointments(): List<Appointment> {
        return dao.selectAppointments().map { it.entityToDomain() }
    }

    override suspend fun selectLocalAppointmentsOfTheYear(
        startOfYear: Long,
        endOfYear: Long
    ): List<Appointment> {
        return dao.selectAppointmentsOfTheYear(startOfYear, endOfYear).map { it.entityToDomain() }
    }

    override suspend fun selectUnsyncedLocalAppointments(): List<Appointment> {
        return dao.selectUnsyncedAppointments().map { it.entityToDomain() }
    }

    override suspend fun upsertLocalAppointment(appointment: Appointment): Long {
        return try {
            dao.upsertAppointment(appointment.domainToEntity())
        } catch (e: Exception) {
            throw Exception(
                "Failed to upsert appointment with ID: ${appointment.id}. Error: ${e.message}",
                e
            )
        }
    }

    override suspend fun deleteLocalAppointment(idList: List<Int>): Int {
        return idList.forEach { dao.deleteAppointment(it) }.toString().toInt()
    }

    override suspend fun getLastIdFromRoom(): Int {
        return try {
            dao.getLastIdFromRoom()
        } catch (e: IOException) {
            throw IOException("IO Exception: ${e.message}")
        } catch (e: Exception) {
            throw Exception("IO Exception: ${e.message}")
        }
    }

    // remote
    override suspend fun getRemoteAppointments(): List<Appointment> {
        return try {
            val res = api.getAppointments()
            if (res.isSuccessful) {
                api.getAppointments().body()?.map { it.dtoToEntity().entityToDomain() }
                    ?: throw Exception("Empty response body")
            } else {
                throw HttpException(res)
            }
        } catch (e: HttpException) {
            throw HttpException(e.response()!!)
        } catch (e: IOException) {
            throw IOException("IO Error: ${e.message}")
        }
    }

    override suspend fun postUnsyncedRemoteAppointments(appointmentList: List<Appointment>): MutableMap<Int, Any> {
        val results = mutableMapOf<Int, Any>()
        for (appointment in appointmentList) {
            try {
                val response = api.postAppointments(appointment.domainToEntity().entityToDto())
                if (response.isSuccessful) {
                    results[response.body()!!.dtoToEntity().entityToDomain().id] =
                        response.isSuccessful
                } else {
                    results[response.body()!!.dtoToEntity().entityToDomain().id] = response.code()
                }
            } catch (e: HttpException) {
                results[appointment.id] = e.code()
            } catch (e: IOException) {
                val errorMessage = e.message ?: "Unknown I/O error occurred"
                results[appointment.id] = errorMessage
            }
        }
        return results
    }

    override suspend fun deleteRemoteAppointments(idList: List<Int>): MutableMap<Int, Boolean> {
        val results = mutableMapOf<Int, Boolean>()
        for (id in idList) {
            try {
                val response = api.deleteAppointment(id)
                if (response.isSuccessful) {
                    results[id] = response.isSuccessful
                } else {
                    results[id] = response.isSuccessful
                }
            } catch (e: HttpException) {
                results[id] = false
            } catch (e: IOException) {
                results[id] = false
            }
        }
        return results
    }

    // cache
    override suspend fun addAppointmentToCache(appointment: Appointment): Resource<Boolean> {
        return try {
            cache.addAppointmentToCache(appointment)
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message}")
        }
    }

    override suspend fun loadAppointmentsFromRepositoryToCache(): Resource<Boolean> {
        return try {
            cache.loadAppointmentsFromRepositoryToCache()
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message}")
        }
    }

    override suspend fun getAllCachedAppointments(): Map<Long, List<Appointment>> {
        return try {
            cache.getAllCachedAppointments()
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message}")
        }
    }

    override suspend fun updateCachedAppointment(appointment: Appointment): Resource<Boolean> {
        return try {
            cache.updateCachedAppointment(appointment)
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message}")
        }
    }

    override suspend fun clearAppointmentCache() {
        cache.clearAppointmentCache()
    }

    override suspend fun deleteAppointmentFromCache(appointment: Appointment): Resource<Boolean> {
        return try {
            cache.deleteAppointmentFromCache(appointment)
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message}")
        }
    }

    override suspend fun getLastIdInCache(): Int? {
        return try {
            cache.getLastIdInCache()
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message}")
        }
    }

    override suspend fun saveLastIdInCache(id: Int): Int {
        return try {
            cache.saveLastIdInCache(id)
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message}")
        }
    }
}
