package com.example.schedule.feature_schedule.domain.use_case.appointment.local

import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLastIdFromRoomUseCase @Inject constructor(
    private val repository: AppointmentRepository,
) {
//    suspend operator fun invoke(): Int {
//        return try {
//            repository.getLastIdFromRoom()
//        } catch (e: IOException) {
//            throw IOException("Exception: ${e.message}")
//        } catch (e: Exception) {
//            throw Exception("Exception: ${e.message}")
//        }
//    }
}