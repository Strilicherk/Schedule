package com.example.schedule.remote

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetAppointmentsFromRemoteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response.error
import java.io.IOException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class GetAppointmentsFromRemoteUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var useCase: GetAppointmentsFromRemoteUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = GetAppointmentsFromRemoteUseCase(repository)
    }

    // Error
    @Test
    @DisplayName("Should emit Loading and Error when IOException occurs")
    fun `should emit Loading and Error when IOException occurs`() = runTest {
        coEvery { repository.getRemoteAppointments() } throws IOException("Device is offline")

        val emissions = useCase().toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)
        assertEquals("I/O Exception: Device is offline", (emissions[1] as Resource.Error).message)

        coVerify(exactly = 1) { repository.getRemoteAppointments() }
    }

    @Test
    @DisplayName("Should emit Loading and Error when HttpException occurs")
    fun `should emit Loading and Error when HttpException occurs`() = runTest {

        fun createHttpException(code: Int, message: String): HttpException {
            val responseBody = ResponseBody.create(null, message)
            val response = error<Any>(code, responseBody)
            return HttpException(response)
        }

        val code = 500
        val message = "Internal Server Error"
        coEvery { repository.getRemoteAppointments() } throws createHttpException(code, message)

        val emissions = useCase().toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Error)
        assertEquals("HTTP Exception: $code - $message", (emissions[1] as Resource.Error).message)

        coVerify(exactly = 1) { repository.getRemoteAppointments() }
    }

    // Success
    @Test
    @DisplayName("Should emit Loading and Success when appointments are fetched successfully")
    fun `should emit Loading and Success when appointments are fetched successfully`() = runTest {
        val appointments = listOf(
            Appointment(
                id = 1,
                title = "Teste 1",
                notes = "English lessons with Edimar",
                color = 1,
                startDate = LocalDate.of(2020, 5, 1),
                endDate = LocalDate.of(2020, 10, 1),
                startTime = LocalTime.of(8, 0),
                endTime = LocalTime.of(10, 0),
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
                startTime = LocalTime.of(8, 0),
                endTime = LocalTime.of(10, 0),
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