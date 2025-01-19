import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.DeleteAppointmentFromRemoteUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.Logger

class DeleteAppointmentFromRemoteUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var logger: Logger
    private lateinit var useCase: DeleteAppointmentFromRemoteUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0])}
        every { logger.error(any()) } answers { println(it.invocation.args[0])}
        every { logger.warn(any()) } answers { println(it.invocation.args[0])}

        useCase = DeleteAppointmentFromRemoteUseCase(repository, logger)
    }

    @Test
    @DisplayName("Should return Resource.Error when appointment is not synced")
    fun shouldReturnResourceErrorWhenAppointmentIsNotSynced() = runTest {
        val appointmentId = 1
        val hasBeenSynced = false

        val result = useCase.invoke(appointmentId, hasBeenSynced)

        assertTrue(result is Resource.Error)
        assertEquals("This appointment can't be deleted in remote because it was not synced", (result as Resource.Error).message)

        coVerify { logger.info("Verifying appointment to delete") }
        coVerify { logger.error("Appointment is not synced yet") }
    }

    @Test
    @DisplayName("Should return Resource.Success when appointment is deleted successfully")
    fun shouldReturnResourceSuccessWhenAppointmentIsDeletedSuccessfully() = runTest {
        val appointmentId = 1
        val hasBeenSynced = true
        coEvery { repository.deleteRemoteAppointment(appointmentId) } returns Resource.Success(true)

        val result = useCase.invoke(appointmentId, hasBeenSynced)

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        coVerify { logger.info("Verifying appointment to delete") }
        coVerify { logger.info("Successfully deleted appointment 1 from remote.") }
        coVerify { repository.deleteRemoteAppointment(appointmentId) }
    }

    @Test
    @DisplayName("Should return Resource.Error when appointment deletion fails")
    fun shouldReturnResourceErrorWhenAppointmentDeletionFails() = runTest {
        val appointmentId = 1
        val hasBeenSynced = true
        coEvery { repository.deleteRemoteAppointment(appointmentId) } returns Resource.Error("Failed to delete appointment from remote")

        val result = useCase.invoke(appointmentId, hasBeenSynced)

        assertTrue(result is Resource.Error)
        assertEquals("Failed to delete appointment from remote", (result as Resource.Error).message)

        coVerify { logger.info("Verifying appointment to delete") }
        coVerify { logger.error("Failed to delete appointment 1 from remote") }
        coVerify { repository.deleteRemoteAppointment(appointmentId) }
    }
}
