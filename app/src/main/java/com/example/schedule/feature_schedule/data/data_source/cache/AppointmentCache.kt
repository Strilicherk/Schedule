package com.example.schedule.feature_schedule.data.data_source.cache

import androidx.collection.mutableIntSetOf
import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentCache @Inject constructor(
    private val repository: AppointmentRepository
) {
    private val cache = mutableMapOf<Long, MutableList<Appointment>>()
    private val toDeleteCache = mutableMapOf<Int, Boolean>()
    var lastIdCache: Int? = null

    private fun formatKey(appointment: Appointment): Long {
        val day = appointment.startDate.dayOfMonth
        val month = appointment.startDate.monthValue
        val year = appointment.startDate.year
        val key = (year * 10000 + month * 100 + day).toLong()
        return key
    }

    suspend fun addAppointmentToCache(appointment: Appointment): Resource<Boolean> {
        val key = formatKey(appointment)
        return try {
            cache.computeIfAbsent(key) { mutableListOf() }.add(appointment)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error("Failed to add appointment: ${e.message}")
        }
    }

    suspend fun loadAppointmentsFromRepositoryToCache(): Resource<Boolean> {
        return try {
            val appointmentsToCache = repository.selectAppointments()
            if (appointmentsToCache.size < 100) {
                appointmentsToCache.forEach { addAppointmentToCache(it) }
            } else {
                appointmentsToCache.chunked(100).forEach { batch ->
                    coroutineScope {
                        batch
                            .map { appointment ->
                                async { addAppointmentToCache(appointment) }
                            }
                            .awaitAll()
                    }
                }
            }
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error("Exception: ${e.message}")
        }
    }

    suspend fun getAllCachedAppointments(): Map<Long, List<Appointment>> {
        return cache.mapValues { it.value.toList() }
    }

    suspend fun updateCachedAppointment(appointment: Appointment): Resource<Boolean> {
        val key = formatKey(appointment)
        return cache[key]?.let { cachedAppointment ->
            val index = cachedAppointment.indexOfFirst { it.id == appointment.id }
            if (index != -1 && cachedAppointment[index] != appointment) {
                cachedAppointment[index] = appointment
                Resource.Success(true)
            } else {
                Resource.Error("Appointment not found or identical to the current one")
            }
        } ?: Resource.Error("No appointments found for the given date")
    }

    suspend fun clearAppointmentCache() {
        cache.clear()
    }

    suspend fun deleteAppointmentFromCache(appointment: Appointment): Resource<Boolean> {
        val key = formatKey(appointment)
        val cachedAppointments = cache[key] ?: return Resource.Error("No appointments found for the given date")

        val index = cachedAppointments.indexOfFirst { it.id == appointment.id }
        if (index == -1) return Resource.Error("Index does not exist")

        return try {
            val appointmentToDelete = cachedAppointments.removeAt(index)
            toDeleteCache[appointmentToDelete.id] = appointmentToDelete.hasBeenSynced
            Resource.Success(true)
        } catch(e: Exception) {
            Resource.Error("Exception: ${e.message}")
        }
    }

    suspend fun saveLastIdInCache(id: Int): Int {
        lastIdCache = id
        return lastIdCache!!
    }

    suspend fun getLastIdInCache(): Int? {
        return lastIdCache
    }
}