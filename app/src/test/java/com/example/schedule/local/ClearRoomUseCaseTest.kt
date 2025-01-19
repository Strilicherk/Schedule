import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.ClearRoomUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.recording.JvmAutoHinter.Companion.exceptionMessage
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.Logger

class ClearRoomUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var logger: Logger
    private lateinit var useCase: ClearRoomUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0])}
        every { logger.error(any()) } answers { println(it.invocation.args[0])}
        every { logger.warn(any()) } answers { println(it.invocation.args[0])}

        useCase = ClearRoomUseCase(repository, logger)
    }

    @Test
    @DisplayName("Should return Resource.Success when clearing local database succeeds")
    fun `should return Resource Success when clearing local database succeeds`() = runTest {
        coEvery { repository.clearAppointmentTable() } returns Resource.Success(true)

        val result = useCase.invoke()

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        coVerify { repository.clearAppointmentTable() }
        coVerify { logger.info("Clearing local database.") }
    }

    @Test
    @DisplayName("Should return Resource.Error when clearing local database fails")
    fun `should return Resource Error when clearing local database fails`() = runTest {
        val errorMessage = "Failed to clear local database"
        coEvery { repository.clearAppointmentTable() } returns Resource.Error(errorMessage)

        val result = useCase.invoke()

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, (result as Resource.Error).message)

        coVerify { repository.clearAppointmentTable() }
        coVerify { logger.info("Clearing local database.") }
    }

    @Test
    @DisplayName("Should return Resource.Error when repository throws an exception")
    fun `should return Resource Error when repository throws an exception`() = runTest {
        coEvery { repository.clearAppointmentTable() } returns Resource.Error("Unable to clear local database")

        val result = useCase.invoke()

        assertTrue(result is Resource.Error)
        assertEquals("Unable to clear local database", (result as Resource.Error).message)

        coVerify { repository.clearAppointmentTable() }
        coVerify { logger.info("Clearing local database.") }
        coVerify { logger.error("Unable to clear local database") }
    }
}
