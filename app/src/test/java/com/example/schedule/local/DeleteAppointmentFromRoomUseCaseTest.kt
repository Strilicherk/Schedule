import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.DeleteAppointmentFromRoomUseCase
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

class DeleteAppointmentFromRoomUseCaseTest {

    private lateinit var repository: AppointmentRepository
    private lateinit var logger: Logger
    private lateinit var useCase: DeleteAppointmentFromRoomUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0])}
        every { logger.error(any()) } answers { println(it.invocation.args[0])}
        every { logger.warn(any()) } answers { println(it.invocation.args[0])}

        useCase = DeleteAppointmentFromRoomUseCase(repository, logger)
    }

    @Test
    @DisplayName("Should return Resource.Success when appointment is successfully deleted")
    fun `should return Resource Success when appointment is successfully deleted`() = runTest {
        val appointmentId = 1

        coEvery { repository.deleteAppointmentFromRoom(appointmentId) } returns Resource.Success(2)

        val result = useCase.invoke(appointmentId)

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        coVerify { repository.deleteAppointmentFromRoom(appointmentId) }
        coVerify { logger.info("Trying to delete appointment in Room") }
        coVerify { logger.info("Appointment deleted") }
    }

    @Test
    @DisplayName("Should return Resource.Error when deleting appointment fails")
    fun `should return Resource Error when deleting appointment fails`() = runTest {
        val appointmentId = 1
        val errorMessage = "Failed to delete appointment"

        coEvery { repository.deleteAppointmentFromRoom(appointmentId) } returns Resource.Error(errorMessage)

        val result = useCase.invoke(appointmentId)

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, (result as Resource.Error).message)

        coVerify { repository.deleteAppointmentFromRoom(appointmentId) }
        coVerify { logger.info("Trying to delete appointment in Room") }
        coVerify { logger.error("Unable to delete appointment: $errorMessage") }
    }
}
