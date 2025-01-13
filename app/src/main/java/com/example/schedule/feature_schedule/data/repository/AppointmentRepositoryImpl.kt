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

    override suspend fun upsertLocalAppointment(appointment: Appointment): Boolean {
        return try {
            dao.upsertAppointment(appointment.domainToEntity())
            true
        } catch (e: Exception) {
            throw Exception(
                "Failed to upsert appointment with ID: ${appointment.id}. Error: ${e.message}", e
            )
        }
    }

    override suspend fun deleteLocalAppointment(idList: List<Int>): Int {
        return idList.forEach { dao.deleteAppointment(it) }.toString().toInt()
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
            Resource.Success(cache.addAppointmentToCache(appointment)) // it never returns false
        } catch (e: IllegalArgumentException) {
            Resource.Error("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message ?: "Unknown Error"}")
        }
    }

    override suspend fun addAppointmentToByDateCache(
        date: Int,
        appointmentId: Int
    ): Resource<Boolean> {
        return try {
            Resource.Success(
                cache.addAppointmentToByDateCache(
                    date,
                    appointmentId
                )
            ) // it never returns false
        } catch (e: IllegalArgumentException) {
            Resource.Error("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message ?: "Unknown Error"}")
        }
    }

    override suspend fun addDateToByAppointmentCache(
        appointmentId: Int,
        date: Int
    ): Resource<Boolean> {
        return try {
            Resource.Success(
                cache.addDateToByAppointmentCache(
                    appointmentId,
                    date
                )
            ) // it never returns false
        } catch (e: IllegalArgumentException) {
            Resource.Error("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message ?: "Unknown Error"}")
        }
    }

    override suspend fun addAppointmentToDeleteCache(
        id: Int,
        hasBeenSynced: Boolean
    ): Resource<Boolean> {
        return try {
            cache.addAppointmentToDeleteCache(id, hasBeenSynced)
            Resource.Success(true)
        } catch (e: IllegalArgumentException) {
            Resource.Error("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message ?: "Unknown Error"}")
        }
    }

    override suspend fun getAllAppointmentsFromCache(): Map<Int, Appointment> {
        return try {
            cache.getAllAppointmentsFromCache()
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message ?: "Unknown Error"}")
        }
    }

    override suspend fun getAllAppointmentsFromByDateCache(): Map<Int, List<Int>> {
        return try {
            cache.getAllAppointmentsFromByDateCache()
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message ?: "Unknown Error"}")
        }
    }

    override suspend fun getAppointmentById(id: Int): Appointment {
        return try {
            cache.getAppointmentById(id) ?: throw Exception("Appointment not found")
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message ?: "Unknown Error"}")
        }
    }

    override suspend fun getDatesByAppointment(id: Int): List<Int> {
        return try {
            cache.getDatesByAppointment(id) ?: emptyList()
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message ?: "Unknown Error"}")
        }
    }

    override suspend fun getLastIdInCache(): Int {
        return try {
            cache.getLastIdInCache()
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message ?: "Unknown Error"}")
        }
    }

    override suspend fun getAppointmentListByDate(id: Int): List<Int> {
        return try {
            cache.getAppointmentListByDate(id)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message ?: "Unknown Error"}")
        }
    }

    override suspend fun updateAppointmentInCache(appointment: Appointment): Resource<Boolean> {
        return try {
            if (cache.updateAppointmentInCache(appointment)) {
                Resource.Success(true)
            } else {
                Resource.Error("Appointment could not be updated.")
            }
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message ?: "Unknown Error"}")
        }
    }

    override suspend fun deleteAppointmentFromCache(appointment: Appointment): Resource<Boolean> {
        return try {
            if (cache.deleteAppointmentFromCache(appointment)) {
                Resource.Success(true)
            } else {
                Resource.Error("Appointment not found.")
            }
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message ?: "Unknown Error"}")
        }
    }

    override suspend fun deleteAppointmentFromDateCache(id: Int): Resource<Boolean> {
        return try {
            if (cache.deleteAppointmentFromDateCache(id)) {
                Resource.Success(true)
            } else {
                Resource.Error("Appointment could not be deleted.")
            }
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message ?: "Unknown Error"}")
        }
    }

    override suspend fun deleteAppointmentFromByDateCache(date: Int, id: Int): Resource<Boolean> {
        return try {
            cache.deleteAppointmentFromByDateCache(date, id)
            Resource.Success(true)
        } catch (e: IllegalArgumentException) {
            Resource.Error("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message ?: "Unknown Error"}")
        }
    }

    override suspend fun clearCache(): Resource<Boolean> {
        return try {
            cache.clearCache()
            if (cache.getAllAppointmentsFromCache()
                    .isEmpty() || cache.getAllAppointmentsFromByDateCache().isEmpty()
            ) {
                Resource.Success(true)
            } else {
                Resource.Error("Unable to clear cache")
            }
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message ?: "Unknown Error"}")
        }
    }

    override suspend fun clearDateByAppointment(id: Int): Resource<Boolean> {
        return try {
            if (cache.clearDateByAppointmentById(id)) Resource.Success(true) else
                Resource.Error("Unable to clear cache")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message ?: "Unknown Error"}")
        }
    }

    override suspend fun generateDateKey(day: Int, month: Int, year: Int): Int {
        return (day * 1000000 + month * 10000 + year)
    }

//    override suspend fun saveLastIdInCache(id: Int): Int? {
//        return cache.saveLastIdInCache(id)
//    }
}
