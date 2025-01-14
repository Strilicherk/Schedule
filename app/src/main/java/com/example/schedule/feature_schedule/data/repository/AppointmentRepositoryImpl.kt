package com.example.schedule.feature_schedule.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
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
    override suspend fun addAppointmentToRoom(appointment: Appointment): Resource<Boolean> {
        return try {
            val createdAppointment = dao.upsertAppointment(appointment.domainToEntity())
            if (createdAppointment == appointment.id.toLong()) {
                Resource.Success(true)
            } else {
                Resource.Error("Failed to create appointment in Room: Mismatch in created ID.")
            }
        } catch (e: SQLiteConstraintException) {
            Resource.Error("Constraint violation while creating appointment: ${e.message}")
        } catch (e: SQLiteException) {
            Resource.Error("Database error while creating appointment: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Unexpected error while creating appointment: ${e.message}")
        }
    }
    override suspend fun getAllAppointmentsFromRoom(): Resource<List<Appointment>> {
        return try {
            val appointments = dao.selectAppointments().map { it.entityToDomain() }
            Resource.Success(appointments)
        } catch (e: SQLiteException) {
            Resource.Error("Database error while fetching appointments: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Unexpected error while fetching appointments: ${e.message}")
        }
    }
    override suspend fun getUnsyncedAppointmentsFromRoom(): Resource<List<Appointment>> {
        return try {
            val appointments = dao.selectUnsyncedAppointments().map { it.entityToDomain() }
            Resource.Success(appointments)
        } catch (e: SQLiteException) {
            Resource.Error("Database error while fetching unsynced appointments: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Unexpected error while fetching unsynced appointments: ${e.message}")
        }
    }
    override suspend fun updateAppointmentInRoom(appointment: Appointment): Resource<Boolean> {
        return try {
            val updatedAppointment = dao.upsertAppointment(appointment.domainToEntity())
            if (updatedAppointment == appointment.id.toLong()) {
                Resource.Success(true)
            } else {
                Resource.Error("Failed to update appointment in Room: Mismatch in updated ID.")
            }
        } catch (e: SQLiteConstraintException) {
            Resource.Error("Constraint violation while updating appointment: ${e.message}")
        } catch (e: SQLiteException) {
            Resource.Error("Database error while updating appointment: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Unexpected error while updating appointment: ${e.message}")
        }
    }
    override suspend fun deleteAppointmentFromRoom(id: Int): Resource<Int> {
        return try {
            val deleteResult = dao.deleteAppointment(id)
            if (deleteResult > 0) {
                Resource.Success(deleteResult)
            } else {
                Resource.Error("No rows were deleted for appointment ID: $id.")
            }
        } catch (e: SQLiteConstraintException) {
            Resource.Error("Constraint violation while deleting appointment: ${e.message}")
        } catch (e: SQLiteException) {
            Resource.Error("Database error while deleting appointment: ${e.message}")
        } catch (e: Exception) {
            Resource.Error("Unexpected error while deleting appointment: ${e.message}")
        }
    }

    // remote
    override suspend fun getRemoteAppointments(): Resource<List<Appointment>> {
        return try {
            val res = api.getAppointments()
            if (res.isSuccessful) {
                api.getAppointments().body()?.map { it.dtoToEntity().entityToDomain() }
                    ?: return Resource.Error("Empty response body")
            } else {
                return Resource.Error("Http error: ${res.code()}")
            }
        } catch (e: HttpException) {
            return Resource.Error("HTTP error: ${e.message()}")
        } catch (e: IOException) {
            return Resource.Error("IO Error: ${e.message}")
        } catch (e: Exception) {
            return Resource.Error("Unexpected error: ${e.message}")
        }
    }
    override suspend fun postUnsyncedRemoteAppointments(appointmentList: List<Appointment>): Resource<MutableMap<Int, Any>> {
        val results = mutableMapOf<Int, Any>()
        for (appointment in appointmentList) {
            try {
                val response = api.postAppointments(appointment.domainToEntity().entityToDto())
                if (response.isSuccessful) {
                    results[response.body()!!.dtoToEntity().entityToDomain().id] = true
                } else {
                    results[response.body()!!.dtoToEntity().entityToDomain().id] = response.code()
                }
            } catch (e: HttpException) {
                results[appointment.id] = e.code()
            } catch (e: IOException) {
                val errorMessage = e.message ?: "Unknown I/O error occurred"
                results[appointment.id] = errorMessage
            } catch (e: Exception) {
                results[appointment.id] = "Unexpected error: ${e.message}"
            }
        }
        return Resource.Success(results)
    }
    override suspend fun deleteRemoteAppointments(idList: List<Int>): Resource<MutableMap<Int, Boolean>> {
        val results = mutableMapOf<Int, Boolean>()
        for (id in idList) {
            try {
                val response = api.deleteAppointment(id)
                if (response.isSuccessful) {
                    results[id] = true
                } else {
                    results[id] = false
                }
            } catch (e: HttpException) {
                results[id] = false
            } catch (e: IOException) {
                results[id] = false
            } catch (e: Exception) {
                results[id] = false
            }
        }
        return Resource.Success(results)
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
    override suspend fun addAppointmentToByDateCache(date: Int, appointmentId: Int): Resource<Boolean> {
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
    override suspend fun addDateToByAppointmentCache(appointmentId: Int, date: Int): Resource<Boolean> {
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
    override suspend fun addAppointmentToDeleteCache(id: Int, hasBeenSynced: Boolean): Resource<Boolean> {
        return try {
            cache.addAppointmentToDeleteCache(id, hasBeenSynced)
            Resource.Success(true)
        } catch (e: IllegalArgumentException) {
            Resource.Error("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message ?: "Unknown Error"}")
        }
    }
    override suspend fun getAllAppointmentsFromCache(): Resource<Map<Int, Appointment>> {
        return try {
            Resource.Success(cache.getAllAppointmentsFromCache())
        } catch (e: IllegalArgumentException) {
            Resource.Error("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message ?: "Unknown Error"}")
        }
    }
    override suspend fun getAllAppointmentsFromByDateCache(): Resource<Map<Int, List<Int>>> {
        return try {
            Resource.Success(cache.getAllAppointmentsFromByDateCache())
        } catch (e: IllegalArgumentException) {
            Resource.Error("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message ?: "Unknown Error"}")
        }
    }
    override suspend fun getAppointmentById(id: Int): Resource<Appointment> {
        return try {
            cache.getAppointmentById(id)?.let {
                Resource.Success(it)
            } ?: Resource.Error("Appointment not found")
        } catch (e: IllegalArgumentException) {
            Resource.Error("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message ?: "Unknown Error"}")
        }
    }
    override suspend fun getDatesByAppointment(id: Int): Resource<List<Int>> {
        return try {
            Resource.Success(cache.getDatesByAppointment(id) ?: emptyList())
        } catch (e: IllegalArgumentException) {
            Resource.Error("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message ?: "Unknown Error"}")
        }
    }
    override suspend fun getLastIdInCache(): Resource<Int> {
        return try {
            val lastId = cache.getLastIdInCache()
            Resource.Success(lastId)
        } catch (e: NoSuchElementException) {
            Resource.Error("NoSuchElementException: ${e.message ?: "Unknown Error"}")
        } catch (e: IllegalArgumentException) {
            Resource.Error("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message ?: "Unknown Error"}")
        }
    }
    override suspend fun getAppointmentListByDate(id: Int): Resource<List<Int>> {
        return try {
            Resource.Success(cache.getAppointmentListByDate(id) ?: emptyList())
        } catch (e: IllegalArgumentException) {
            Resource.Error("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message ?: "Unknown Error"}")
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
            Resource.Error("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message ?: "Unknown Error"}")
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
            Resource.Error("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message ?: "Unknown Error"}")
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
            Resource.Error("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message ?: "Unknown Error"}")
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
}
