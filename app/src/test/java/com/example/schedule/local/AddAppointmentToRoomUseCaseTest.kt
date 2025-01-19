package com.example.schedule.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.ValidateAppointmentInfosUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.AddAppointmentToRoomUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.recording.JvmAutoHinter.Companion.exceptionMessage
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import java.time.LocalDate
import java.time.LocalTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class AddAppointmentToRoomUseCaseTest {

    private lateinit var repository: AppointmentRepository
    private lateinit var validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase
    private lateinit var useCase: AddAppointmentToRoomUseCase
    private lateinit var logger: Logger

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        validateAppointmentInfosUseCase = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0])}
        every { logger.error(any()) } answers { println(it.invocation.args[0])}
        every { logger.warn(any()) } answers { println(it.invocation.args[0])}

        useCase = AddAppointmentToRoomUseCase(repository, validateAppointmentInfosUseCase, logger)
    }

    @Test
    @DisplayName("Should return Resource.Error when validation fails")
    fun `should return Resource Error when validation fails`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "",
            notes = "Invalid Appointment",
            color = 0,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now(),
        )

        coEvery { validateAppointmentInfosUseCase.invoke(appointment) } returns Resource.Error("Validation failed")

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Error)
        assertEquals("Validation failed", (result as Resource.Error).message)

        coVerify(exactly = 1) { validateAppointmentInfosUseCase.invoke(appointment) }
        coVerify(exactly = 0) { repository.addAppointmentToRoom(any()) }
        coVerify { logger.error("Validation failed: Validation failed") }
    }

    @Test
    @DisplayName("Should return Resource.Success when appointment is successfully added to Room")
    fun `should return Resource Success when appointment is successfully added to Room`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Valid Appointment",
            notes = "Create Test",
            color = 1,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
        )

        coEvery { validateAppointmentInfosUseCase.invoke(appointment) } returns Resource.Success(appointment)
        coEvery { repository.addAppointmentToRoom(appointment) } returns Resource.Success(true)

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        coVerify(exactly = 1) { validateAppointmentInfosUseCase.invoke(appointment) }
        coVerify(exactly = 1) { repository.addAppointmentToRoom(appointment) }
        coVerify { logger.info("Validating appointment information.") }
        coVerify { logger.info("Adding appointment to Room.") }
    }

    @Test
    @DisplayName("Should return Resource.Error when repository returns an exception")
    fun `should return Resource Error when repository throws an exception`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Valid Appointment",
            notes = "Create Test",
            color = 1,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
        )

        coEvery { validateAppointmentInfosUseCase.invoke(appointment) } returns Resource.Success(appointment)
        coEvery { repository.addAppointmentToRoom(appointment) } returns Resource.Error("Failed to add appointment to Room")

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Error)
        assertEquals("Failed to add appointment to Room", (result as Resource.Error).message)

        coVerify(exactly = 1) { validateAppointmentInfosUseCase.invoke(appointment) }
        coVerify(exactly = 1) { repository.addAppointmentToRoom(appointment) }
        coVerify { logger.info("Validating appointment information.") }
        coVerify { logger.info("Adding appointment to Room.") }
        coVerify { logger.error("Failed to add appointment to Room") }
    }
}
