package com.example.schedule.cache

import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.ValidateAppointmentInfosUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.AddAppointmentToCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpsertLocalAppointmentUseCase
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class AddAppointmentToCacheUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var useCase: AddAppointmentToCacheUseCase
    private lateinit var upsertLocalAppointmentUseCase: UpsertLocalAppointmentUseCase
    private lateinit var validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase

    @BeforeEach
    fun Setup() {
        repository = mockk()
        useCase = AddAppointmentToCacheUseCase(repository, validateAppointmentInfosUseCase, upsertLocalAppointmentUseCase)
    }

//    @Test
//    @DisplayName()
//    fun
}