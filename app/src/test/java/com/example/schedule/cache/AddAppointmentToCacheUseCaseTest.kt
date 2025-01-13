package com.example.schedule.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.ValidateAppointmentInfosUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.AddAppointmentToCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.AddAppointmentToRoomUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalTime

class AddAppointmentToCacheUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase
    private lateinit var addAppointmentToRoomUseCase: AddAppointmentToRoomUseCase
    private lateinit var useCase: AddAppointmentToCacheUseCase

    @BeforeEach
    fun Setup() {
        repository = mockk()
        validateAppointmentInfosUseCase = mockk()
        addAppointmentToRoomUseCase = mockk()
        useCase = AddAppointmentToCacheUseCase(repository, validateAppointmentInfosUseCase, addAppointmentToRoomUseCase)
    }

    @Test
    @DisplayName("Should return Resource.Error when there is some error in appointment validation")
    fun `should return Resource Error when appointment title is blank` () = runTest {
        val appointment = Appointment(
            id = 1,
            title = "",
            notes = "",
            color = 0,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now()
        )

        coEvery { validateAppointmentInfosUseCase.invoke(appointment) } returns Resource.Error("Appointment title cannot be empty")

        val result = useCase.invoke(appointment)
        assertTrue(result is Resource.Error)
    }

    @Test
    @DisplayName("Should return Resource.Error when failed to add appointment to cache")
    fun `should return Resource Error when failed to add appointment to cache` () = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Test",
            notes = "",
            color = 0,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now()
        )

        coEvery { validateAppointmentInfosUseCase.invoke(appointment) } returns Resource.Success(appointment)
        coEvery { repository.addAppointmentToCache(appointment) } returns Resource.Error("Unknown Error.")

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Error)
        assertEquals("Failed to add appointment to cache.", (result as Resource.Error).message)

        coVerify { repository.addAppointmentToCache(appointment) }
    }

    @Test
    @DisplayName("Should return Resource.Error when failed to add appointment to room")
    fun `should return Resource Error when failed to add appointment to room` () = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Test",
            notes = "",
            color = 0,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now()
        )

        coEvery { validateAppointmentInfosUseCase.invoke(appointment) } returns Resource.Success(appointment)
        coEvery { repository.addAppointmentToCache(appointment) } returns Resource.Success(true)
        coEvery { addAppointmentToRoomUseCase.invoke(appointment) } returns Resource.Error("Exception")

        val result = useCase.invoke(appointment)
        assertTrue(result is Resource.Error)
        assertEquals("Failed to create appointment in Room.", (result as Resource.Error).message)
    }
}