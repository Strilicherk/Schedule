package com.example.schedule.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpsertLocalAppointmentUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpsertLocalAppointmentUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var useCase: UpsertLocalAppointmentUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = UpsertLocalAppointmentUseCase(repository)
    }

    @Test
    @DisplayName("Should emit Loading and Error when appointment title is blank")
    fun `should emit Loading and Error when appointment title is blank`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "",
            notes = "Title Testing",
            color = 1,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now()
        )

        val emissions = useCase(appointment).toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)
        assertEquals("Appointment title cannot be empty",(emissions[1] as Resource.Error).message)

        coVerify (exactly = 0) { repository.upsertLocalAppointment(any()) }
    }

    @Test
    @DisplayName("Should emit Loading and Error when appointment start date is after end date")
    fun `should emit Loading and Error when appointment start date is after end date`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Test",
            notes = "Date Testing",
            color = 1,
            startDate = LocalDate.of(2024, 12, 10),
            endDate = LocalDate.of(2024, 12, 5),
            startTime = LocalTime.now(),
            endTime = LocalTime.now()
        )

        val emissions = useCase(appointment).toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)
        assertEquals("Appointment start date must be before end date", (emissions[1] as Resource.Error).message)

        coVerify (exactly = 0) { repository.upsertLocalAppointment(any()) }
    }

    @Test
    @DisplayName("Should emit Loading and Error when appointment start time is after end time")
    fun `should emit Loading and Error when appointment start time is after end time`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Test",
            notes = "Time Testing",
            color = 1,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(17,0),
            endTime = LocalTime.of(13,0)
        )

        val emissions = useCase(appointment).toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)
        assertEquals("Appointment start time must be before end time",(emissions[1] as Resource.Error).message)

        coVerify (exactly = 0) { repository.upsertLocalAppointment(any()) }
    }

    @Test
    @DisplayName("Should emit Loading and Error when repository throws an exception")
    fun `should emit Loading and Success when repository throws an exception`() = runTest {
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

        coEvery { repository.upsertLocalAppointment(appointment) } throws IOException("Database Error")

        val emissions = useCase(appointment).toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)
        assertEquals(("Exception: Database Error"), (emissions[1] as Resource.Error).message)

        coVerify (exactly = 1) { repository.upsertLocalAppointment(appointment) }
    }

    @Test
    @DisplayName("Should emit Loading and Success when appointment was successfully created")
    fun `should emit Loading and Success when appointment was successfully created`() = runTest {
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

        coEvery { repository.upsertLocalAppointment(appointment) } returns 1

        val emissions = useCase(appointment).toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
        assertEquals(true, (emissions[1] as Resource.Success).data)

        coVerify (exactly = 1) { repository.upsertLocalAppointment(appointment) }
    }
}