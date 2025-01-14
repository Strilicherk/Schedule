package com.example.schedule.local

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.UpsertRemoteAppointmentsIntoRoomUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UpsertRemoteAppointmentsIntoRoomUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var useCase: UpsertRemoteAppointmentsIntoRoomUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = UpsertRemoteAppointmentsIntoRoomUseCase(repository)
    }

    // Error
    @Test
    @DisplayName("Should emit Loading and Error when")
    fun `should emit Loading and Error when` () = runTest {

    }

    // Success
    @Test
    @DisplayName("Should emit Loading and Success when apiAppointmentList is empty")
    fun `should emit Loading and Error when apiAppointmentsList is empty` () = runTest {
        val remoteAppointments = flowOf(Resource.Success(emptyList<Appointment>()))

        val emissions = useCase(remoteAppointments).toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is Resource.Loading)
        assertTrue(emissions[1] is Resource.Success)
        assertEquals(true, (emissions[1] as Resource.Success).data)

        coVerify (exactly = 0) { repository.updateAppointmentInRoom(any()) }
    }
}