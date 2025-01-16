package com.example.schedule.remote

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.PostUnsyncedAppointmentInRemoteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlinx.coroutines.test.runTest
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class PostUnsyncedAppointmentInRemoteUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var useCase: PostUnsyncedAppointmentInRemoteUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = PostUnsyncedAppointmentInRemoteUseCase(repository)
    }

    // Error
    @Test
    @DisplayName("Should emit Loading and Error when some appointment cannot be synced because of HttpException")
    fun `should emit Loading and Error when some appointment cannot be synced because of httpException`() =
        runTest {
            val unsyncedAppointments = listOf(
                Appointment(
                    id = 1,
                    title = "UseCase Unit Tests",
                    notes = "Write unit tests for each use case",
                    color = 1,
                    startDate = LocalDate.now(),
                    endDate = LocalDate.now(),
                    startTime = LocalTime.now(),
                    endTime = LocalTime.now(),
                    lastModified = LocalDateTime.now(),
                    isSynced = false
                ),
                Appointment(
                    id = 2,
                    title = "Caching",
                    notes = "Write caching logic",
                    color = 1,
                    startDate = LocalDate.now(),
                    endDate = LocalDate.now(),
                    startTime = LocalTime.now(),
                    endTime = LocalTime.now(),
                    lastModified = LocalDateTime.now(),
                    isSynced = false
                )
            )
            coEvery { repository.getUnsyncedAppointmentsFromRoom() } returns unsyncedAppointments
            coEvery { repository.addAppointmentToRemote(unsyncedAppointments) } returns mutableMapOf(
                1 to true,
                2 to 404,
            )

            val emissions = useCase().toList()

            assertEquals(2, emissions.size)
            assertTrue(emissions[0] is Resource.Loading)
            assertTrue(emissions[1] is Resource.Error)
            assertTrue((emissions[1] as Resource.Error).data == false)

            coVerify(exactly = 1) { repository.getUnsyncedAppointmentsFromRoom() }
            coVerify(exactly = 1) { repository.addAppointmentToRemote(unsyncedAppointments) }
        }

    @Test
    @DisplayName("Should emit Loading and Error when some appointment cannot be synced because of IOException")
    fun `should emit Loading and Error when some appointment cannot be synced because of IOException`() =
        runTest {
            val unsyncedAppointments = listOf(
                Appointment(
                    id = 1,
                    title = "UseCase Unit Tests",
                    notes = "Write unit tests for each use case",
                    color = 1,
                    startDate = LocalDate.now(),
                    endDate = LocalDate.now(),
                    startTime = LocalTime.now(),
                    endTime = LocalTime.now(),
                    lastModified = LocalDateTime.now(),
                    isSynced = false
                ),
                Appointment(
                    id = 2,
                    title = "Caching",
                    notes = "Write caching logic",
                    color = 1,
                    startDate = LocalDate.now(),
                    endDate = LocalDate.now(),
                    startTime = LocalTime.now(),
                    endTime = LocalTime.now(),
                    lastModified = LocalDateTime.now(),
                    isSynced = false
                )
            )
            coEvery { repository.getUnsyncedAppointmentsFromRoom() } returns unsyncedAppointments
            coEvery { repository.addAppointmentToRemote(unsyncedAppointments) } returns mutableMapOf(
                1 to true,
                2 to "Unknown I/O error occurred",
            )

            val emissions = useCase().toList()

            assertEquals(2, emissions.size)
            assertTrue(emissions[0] is Resource.Loading)
            assertTrue(emissions[1] is Resource.Error)
            assertTrue((emissions[1] as Resource.Error).data == false)

            coVerify(exactly = 1) { repository.getUnsyncedAppointmentsFromRoom() }
            coVerify(exactly = 1) { repository.addAppointmentToRemote(unsyncedAppointments) }
        }

    // Success
    @Test
    @DisplayName("Should emit Loading and Success when unsyncedLocalAppointments is empty")
    fun `should emit Loading and Success when unsyncedLocalAppointments is empty`() = runTest {
        coEvery { repository.getUnsyncedAppointmentsFromRoom() } returns emptyList<Appointment>()

        val emissions = useCase().toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
        assertTrue((emissions[1] as Resource.Success).data == true)

        coVerify(exactly = 1) { repository.getUnsyncedAppointmentsFromRoom() }
    }

    @Test
    @DisplayName("Should emit Loading and Success when all local appointments have been synced")
    fun `should emit Loading and Success when all local appointments have been synced`() = runTest {
        val unsyncedAppointments = listOf(
            Appointment(
                id = 1,
                title = "UseCase Unit Tests",
                notes = "Write unit tests for each use case",
                color = 1,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now(),
                lastModified = LocalDateTime.now(),
                isSynced = false
            ),
            Appointment(
                id = 2,
                title = "Caching",
                notes = "Write caching logic",
                color = 1,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now(),
                lastModified = LocalDateTime.now(),
                isSynced = false
            )
        )
        coEvery { repository.getUnsyncedAppointmentsFromRoom() } returns unsyncedAppointments
        coEvery { repository.addAppointmentToRemote(unsyncedAppointments) } returns mutableMapOf(
            1 to true,
            2 to true,
        )

        val emissions = useCase().toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
        assertTrue((emissions[1] as Resource.Success).data == true)

        coVerify(exactly = 1) { repository.getUnsyncedAppointmentsFromRoom() }
        coVerify(exactly = 1) { repository.addAppointmentToRemote(unsyncedAppointments) }
    }
}
