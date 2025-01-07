package com.example.schedule.remote

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.DeleteRemoteAppointmentUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeleteRemoteAppointmentUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var useCase: DeleteRemoteAppointmentUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = DeleteRemoteAppointmentUseCase(repository)
    }

//    // Error
//    @Test
//    @DisplayName("Should emit Loading and Error when some appointments fail to delete")
//    fun `should emit Loading and Error when some appointments fail to delete`() = runTest {
//        val appointmentsToDelete = listOf(1,2,3,4)
//        val mockResponse = mutableMapOf(1 to true, 2 to true, 3 to true, 4 to false)
//        val failedAppointments = setOf(4)
//        coEvery { repository.deleteRemoteAppointments(appointmentsToDelete) } returns mockResponse
//
//        val emissions = useCase(appointmentsToDelete).toList()
//
//        assertEquals(2, emissions.size)
//        assertTrue(emissions[0] is Resource.Loading)
//        assertTrue(emissions[1] is Resource.Error)
//        assertEquals("Failed to delete appointments: $failedAppointments",(emissions[1] as Resource.Error).message)
//
//        coVerify (exactly = 1) { repository.deleteRemoteAppointments(appointmentsToDelete) }
//    }
//
//    // Success
//    @Test
//    @DisplayName("Should emit Loading and Success when pendingDeletion is empty")
//    fun `should emit Loading and Success when pendingDeletion is empty`() = runTest {
//        val appointmentsToDelete = emptyList<Int>()
//
//        coVerify(exactly = 0) { repository.deleteRemoteAppointments(any()) }
//
//        val emissions = useCase(appointmentsToDelete).toList()
//
//        assertEquals(2, emissions.size)
//        assertTrue(emissions[0] is Resource.Loading)
//        assertTrue(emissions[1] is Resource.Success)
//        assertTrue((emissions[1] as Resource.Success).data == true)
//    }
//
//    @Test
//    @DisplayName("Should emit Loading and Success when all appointments are deleted successfully")
//    fun `should emit Loading and Error when all appointments are deleted successfully`() = runTest {
//        val appointmentsToDelete = listOf(1,2,3,4)
//        val mockResponse = mutableMapOf(1 to true, 2 to true, 3 to true, 4 to true)
//
//        coEvery { repository.deleteRemoteAppointments(appointmentsToDelete) } returns mockResponse
//
//        val emissions = useCase(appointmentsToDelete).toList()
//
//        assertEquals(2, emissions.size)
//        assertTrue(emissions[0] is Resource.Loading)
//        assertTrue(emissions[1] is Resource.Success)
//        assertTrue((emissions[1] as Resource.Success).data == true)
//
//        coVerify (exactly = 1) { repository.deleteRemoteAppointments(appointmentsToDelete) }
//    }
}