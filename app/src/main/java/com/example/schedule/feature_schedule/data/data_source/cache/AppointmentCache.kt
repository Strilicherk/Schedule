package com.example.schedule.feature_schedule.data.data_source.cache

import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentCache {
    private val appointmentByDayCache = mutableMapOf<Int, MutableList<Int>>()
    private val appointmentCache = mutableMapOf<Int, Appointment>()
    private val toDeleteCache = mutableMapOf<Int, Boolean>()
    private var lastIdCache: Int? = null

    private fun formatKey(day: Int, month: Int, year: Int): Int {
        val key = (day * 1000000 + month * 10000 + year)
        return key
    }

    fun addAppointmentToCache(appointment: Appointment) {
        appointmentCache[appointment.id] = appointment
    }

    fun addAppointmentToByDayCache(date: Int, appointmentId: Int) {
        appointmentByDayCache.computeIfAbsent(date) { mutableListOf() }.add(appointmentId)
    }

    fun addAppointmentToDeleteList(id: Int, hasBeenSynced: Boolean) {
        toDeleteCache[id] = hasBeenSynced
    }

    fun getAllAppointmentsFromCache(): Map<Int, Appointment> {
        return appointmentCache
    }

    fun getAllAppointmentsFromByDayCache(): Map<Int, List<Int>> {
        return appointmentByDayCache
    }

    fun getAppointmentById(id: Int): Appointment? {
        return appointmentCache[id]
    }

    fun getLastIdInCache(): Int {
        return appointmentCache.keys.last()
    }

    fun updateAppointmentInCache(appointment: Appointment): Boolean {
        return if (appointmentCache.containsKey(appointment.id)) {
            appointmentCache[appointment.id] = appointment
            true
        } else {
            false
        }
    }

    fun deleteAppointmentFromCache(appointment: Appointment): Boolean {
        return if (appointmentCache.containsKey(appointment.id)) {
            appointmentCache.remove(appointment.id)
            true
        } else {
            false
        }
    }

    fun deleteAppointmentFromByDayCache(date: Int, id: Int) {
        appointmentByDayCache[date]?.let {
            it.remove(id)
            if (it.isEmpty()) {
                appointmentByDayCache.remove(date)
            }
        }
    }

    fun clearCache() {
        appointmentCache.clear()
        appointmentByDayCache.clear()
    }


}

//fun saveLastIdInCache(id: Int): Int? {
//        lastIdCache = id
//        return lastIdCache
//    }

//    private fun addAppointmentInByDayCache(key: Int, appointmentId: Int) {
//        generateSequence(appointment.startDate) { current ->
//            if (current.isBefore(appointment.endDate) || current.isEqual(appointment.endDate)) current.plusDays(1) else null
//        }.forEach { date ->
//            val key = formatKey(date.dayOfMonth, date.monthValue, date.year)
//            appointmentByDayCache.computeIfAbsent(key) { mutableListOf() }.add(appointment.id)
//        }
//    }

//    suspend fun addAppointmentToCache(appointment: Appointment): Resource<Boolean> {
//        val key = appointment.id
//        return try {
//            appointmentCache.computeIfAbsent(key) { appointment }
//            withContext(Dispatchers.IO) {
//                addAppointmentsInByDayCache(appointment.id, appointment.startDate, appointment.endDate)
//            }
//            Resource.Success(true)
//        } catch (e: Exception) {
//            Resource.Error("Failed to add appointment: ${e.message}")
//        }
//    }
//
//    suspend fun addAppointmentsInByDayCache(id: Int, startDate: LocalDate, endDate: LocalDate): Resource<Boolean> {
//        return try {
//            val dateList = generateSequence(startDate) { current ->
//                if (current.isBefore(endDate) || current.isEqual(endDate)) current.plusDays(1) else null
//            }
//            dateList.forEach { date ->
//                val key = formatKey(date.dayOfMonth, date.monthValue, date.year)
//                appointmentByDayCache.computeIfAbsent(key) { mutableListOf() }.add(id)
//            }
//            Resource.Success(true)
//        } catch (e: IllegalArgumentException) {
//            Resource.Error("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
//        } catch (e: NullPointerException) {
//            Resource.Error("NullPointerException: ${e.message ?: "Unknown Error"}")
//        } catch (e: Exception) {
//            Resource.Error("Exception: ${e.message}")
//        }
//    }
//
//    suspend fun getAppointmentsFromRepository(): Resource<List<Appointment>> {
//        return try {
//            val appointmentsToCache = repository.selectAppointments()
//            Resource.Success(appointmentsToCache)
//        } catch (e: Exception) {
//            Resource.Error("Exception: ${e.message}")
//        }
//    }
//
//    suspend fun loadAppointmentsFromRepositoryToCache(): Resource<Boolean> {
//        return try {
//            var hasError = false
//            val appointmentsToCache = getAppointmentsFromRepository().data ?: emptyList()
//
//            if (appointmentsToCache.isEmpty()) {
//                return Resource.Error("No appointments to load.")
//            }
//
//            if (appointmentsToCache.size < 100) {
//                appointmentsToCache.forEach {
//                    val result = addAppointmentToCache(it)
//                    if (result is Resource.Error) {
//                        hasError = true
//                        println("Failed to add appointment ${it.id}")
//                    }
//                }
//            } else {
//                appointmentsToCache.chunked(100).forEach { batch ->
//                    val results = coroutineScope {
//                        batch.map { appointment ->
//                            async {
//                                addAppointmentToCache(appointment)
//                            }
//                        }.awaitAll()
//                    }
//
//                    if (results.any { it is Resource.Error }) {
//                        hasError = true
//                        println("Failed to add one or more appointments in the batch.")
//                    }
//                }
//            }
//
//            if (hasError) {
//                Resource.Error("One or more appointments failed to load.")
//            } else {
//                Resource.Success(true)
//            }
//
//        } catch (e: Exception) {
//            Resource.Error("Exception: ${e.message ?: "Unknown error"}")
//        }
//    }
//
//    suspend fun updateCachedAppointment(key: Int, appointment: Appointment): Resource<Boolean> {
//        return try {
//            if (appointmentCache.containsKey(key)) {
//                appointmentCache[key] = appointment
//                Resource.Success(true)
//            } else {
//                Resource.Error("Appointment not found in cache")
//            }
//        } catch (e: IllegalArgumentException) {
//            Resource.Error("IllegalArgumentException: ${e.message ?: "Unknown Error"}")
//        } catch (e: NullPointerException) {
//            Resource.Error("NullPointerException: ${e.message ?: "Unknown Error"}")
//        }
//    }
//
//    suspend fun deleteAppointmentFromCache(dateKey: Int, appointment: Appointment): Resource<Boolean> {
//        return appointmentByDayCache[dateKey]?.let {
//            if (it.remove(appointment.id)) {
//                appointmentCache.remove(appointment.id)
//                toDeleteCache[appointment.id] = appointment.hasBeenSynced
//                Resource.Success(true)
//            } else {
//                Resource.Error("Appointment ID not found for the specified date.")
//            }
//        } ?: Resource.Error("No appointments found for the specified date.")
//    }
//
//    suspend fun clearAppointmentCache() {
//        appointmentCache.clear()
//    }
//

