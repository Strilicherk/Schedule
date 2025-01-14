package com.example.schedule.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
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

class AddAppointmentToRoomUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var useCase: AddAppointmentToRoomUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = AddAppointmentToRoomUseCase(repository)
    }

    @Test
    @DisplayName("Should return Resource Error when repository throws an exception")
    fun `should return Resource Error when repository throws an exception`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Test",
            notes = "Create Test",
            color = 1,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
        )

        val message = "Failed to upsert appointment with ID: 1. Error: Cannot access database"
        coEvery { repository.updateAppointmentInRoom(appointment) } throws Exception(message)

        val result = useCase.invoke(appointment)
        assertTrue(result is Resource.Error)
        assertEquals("General Exception: $message", (result as Resource.Error).message)

        coVerify (exactly = 1) { repository.updateAppointmentInRoom(appointment) }
    }

    @Test
    @DisplayName("Should return Resource Success when appointment was successfully created")
    fun `should return Resource Success when appointment was successfully created`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Test",
            notes = "Create Test",
            color = 1,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
        )

        coEvery { repository.updateAppointmentInRoom(appointment) } returns 1

        val result = useCase.invoke(appointment)
        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        coVerify (exactly = 1) { repository.updateAppointmentInRoom(appointment) }
    }
}