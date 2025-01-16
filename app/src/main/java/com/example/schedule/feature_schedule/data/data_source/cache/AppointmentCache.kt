package com.example.schedule.feature_schedule.data.data_source.cache

import com.example.schedule.feature_schedule.domain.model.Appointment
import javax.inject.Singleton

@Singleton
class AppointmentCache {
    private val appointmentByDateCache = mutableMapOf<Int, MutableList<Int>>() // 09012025: [1,2,3]
    private val dateByAppointmentCache =
        mutableMapOf<Int, MutableList<Int>>() // 1: [09012025,10012025,11012025]
    private val appointmentCache = mutableMapOf<Int, Appointment>()
    private val appointmentToDeleteCache = mutableMapOf<Int, Boolean>()
    private var lastIdCache: Int? = null

    fun addAppointmentToCache(appointment: Appointment): Boolean {
        if (!appointmentCache.containsKey(appointment.id)) appointmentCache[appointment.id] =
            appointment
        return true
    }
    fun addAppointmentToByDateCache(date: Int, appointmentId: Int): Boolean {
        if (appointmentByDateCache[date]?.contains(appointmentId) != true) appointmentByDateCache.computeIfAbsent(
            date
        ) { mutableListOf() }.add(appointmentId)
        return true
    }
    fun addDateToByAppointmentCache(appointmentId: Int, date: Int): Boolean {
        if (dateByAppointmentCache[appointmentId]?.contains(date) != true) dateByAppointmentCache.computeIfAbsent(
            appointmentId
        ) { mutableListOf() }.add(date)
        return true
    }
    fun addAppointmentToDeleteCache(id: Int, hasBeenSynced: Boolean): Boolean {
        if (!appointmentToDeleteCache.containsKey(id)) appointmentToDeleteCache[id] = hasBeenSynced
        return true
    }
    fun getAllAppointmentsFromCache(): Map<Int, Appointment> {
        return appointmentCache
    }
    fun getAllAppointmentsFromByDateCache(): Map<Int, List<Int>> {
        return appointmentByDateCache
    }
    fun getDatesByAppointment(id: Int): List<Int>? {
        return dateByAppointmentCache[id]?.toList()
    }
    fun getAppointmentById(id: Int): Appointment? {
        return appointmentCache[id]
    }
    fun getAppointmentListByDate(id: Int): List<Int>? {
        return appointmentByDateCache[id]?.toList()
    }
    fun getAppointmentsFromDeleteList(): Map<Int, Boolean> {
        return appointmentToDeleteCache
    }
    fun getLastIdInCache(): Int {
        return appointmentCache.keys.last()
    }
    fun updateAppointmentInCache(appointment: Appointment): Boolean {
        if (appointmentCache.containsKey(appointment.id)) appointmentCache[appointment.id] =
            appointment
        return appointmentCache[appointment.id] == appointment
    }
    fun deleteAppointmentFromCache(appointment: Appointment): Boolean {
        return if (appointmentCache.containsKey(appointment.id)) {
            appointmentCache.remove(appointment.id)
            !appointmentCache.containsKey(appointment.id)
        } else {
            true
        }
    }
    fun deleteAppointmentFromByDateCache(date: Int, id: Int) {
        appointmentByDateCache[date]?.let {
            it.remove(id)
            if (it.isEmpty()) {
                appointmentByDateCache.remove(date)
            }
        }
    }
    fun deleteAppointmentFromDateCache(id: Int): Boolean {
        return if (dateByAppointmentCache.containsKey(id)) {
            dateByAppointmentCache.remove(id)
            !dateByAppointmentCache.containsKey(id)
        } else {
            true
        }
    }
    fun deleteAppointmentFromDeleteCache(id: Int): Boolean {
        return if (appointmentToDeleteCache.containsKey(id)) {
            appointmentToDeleteCache.remove(id)
            !appointmentToDeleteCache.containsKey(id)
        } else {
            true
        }
    }
    fun clearDateByAppointmentById(id: Int): Boolean {
        dateByAppointmentCache[id]?.clear()
        return true
    }
    fun clearCache() {
        appointmentCache.clear()
        appointmentByDateCache.clear()
        dateByAppointmentCache.clear()
    }

}

//fun saveLastIdInCache(id: Int): Int? {
//        lastIdCache = id
//        return lastIdCache
//    }

//    private fun addAppointmentInByDayCache(key: Int, appointmentId: Int) {
//
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

