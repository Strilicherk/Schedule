package com.example.schedule

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetRemoteAppointmentsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class GetRemoteAppointmentsUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var useCase: GetRemoteAppointmentsUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = GetRemoteAppointmentsUseCase(repository)
    }

    @Test
    @DisplayName("Should emit Loading and Success when appointments are fetched successfully")
    fun `should emit Loading and Success when appointments are fetched successfully` () = runTest {
        val appointments = listOf(
            Appointment(
                id = 1,
                title = "Teste 1",
                notes = "English lessons with Edimar",
                color = 1,
                startDate = LocalDate.of(2020, 5, 1),
                endDate = LocalDate.of(2020, 10, 1),
                startTime = LocalTime.of(8,0),
                endTime = LocalTime.of(10,0),
                lastModified = LocalDateTime.now(),
                isSynced = false
            ),
            Appointment(
                id = 2,
                title = "Teste 2",
                notes = "English lessons with Edimar",
                color = 1,
                startDate = LocalDate.of(2024, 5, 1),
                endDate = LocalDate.of(2024, 10, 1),
                startTime = LocalTime.of(8,0),
                endTime = LocalTime.of(10,0),
                lastModified = LocalDateTime.now(),
                isSynced = true
            )
        )

        coEvery { repository.getRemoteAppointments() } returns appointments

        val emissions = useCase().toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
        assertEquals(appointments, (emissions[1] as Resource.Success).data)

        coVerify(exactly = 1) { repository.getRemoteAppointments() }
    }
}