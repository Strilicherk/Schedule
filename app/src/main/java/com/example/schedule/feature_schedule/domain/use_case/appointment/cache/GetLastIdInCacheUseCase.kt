package com.example.schedule.feature_schedule.domain.use_case.appointment.cache

import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.GetLastIdFromRoomUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetLastIdInCacheUseCase @Inject constructor(
    private val repository: AppointmentRepository,
    private val getLastIdFromRoomUseCase: GetLastIdFromRoomUseCase,
    private val saveLastIdInCacheUseCase: SaveLastIdInCacheUseCase
){
    suspend operator fun invoke(): Int {
        return try {
            val lastId = repository.getLastIdInCache()
            if (lastId == null) {
                val lastIdFromRoom = getLastIdFromRoomUseCase.invoke()
                val res = saveLastIdInCacheUseCase.invoke(lastIdFromRoom)

                if (res.data == false) throw Exception("Cannot save last id from Room")

                lastIdFromRoom
            } else lastId
        } catch (e: Exception) {
            throw Exception("Exception: ${e.message}")
        }
    }
}