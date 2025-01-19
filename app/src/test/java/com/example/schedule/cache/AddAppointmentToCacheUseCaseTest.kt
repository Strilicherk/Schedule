package com.example.schedule.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.ValidateAppointmentInfosUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.AddAppointmentToCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.AddAppointmentToRoomUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import java.time.LocalDate
import java.time.LocalTime

class AddAppointmentToCacheUseCaseTest{
    private lateinit var repository: AppointmentRepository
    private lateinit var validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase
    private lateinit var addAppointmentToRoomUseCase: AddAppointmentToRoomUseCase
    private lateinit var useCase: AddAppointmentToCacheUseCase
    private lateinit var logger: Logger

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        validateAppointmentInfosUseCase = mockk(relaxed = true)
        addAppointmentToRoomUseCase = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0]) }
        every { logger.warn(any()) } answers { println(it.invocation.args[0]) }
        every { logger.error(any()) } answers { println(it.invocation.args[0]) }

        useCase = AddAppointmentToCacheUseCase(repository, validateAppointmentInfosUseCase, addAppointmentToRoomUseCase, logger)
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

        coEvery { validateAppointmentInfosUseCase.invoke(appointment) } returns Resource.Error("Validation failed")

        val result = useCase.invoke(appointment)
        assertTrue(result is Resource.Error)
        assertEquals("Validation failed", (result as Resource.Error).message)

        coVerify { validateAppointmentInfosUseCase.invoke(appointment) }
        coVerify(exactly = 0) { addAppointmentToRoomUseCase.invoke(any()) }
        coVerify(exactly = 0) { repository.addAppointmentToCache(any()) }
    }

    @Test
    @DisplayName("Should return Resource.Error when failed to add appointment to room")
    fun `should return Resource Error when failed to add appointment to room` () = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Valid title",
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