import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.DeleteAppointmentFromRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.sync.ProcessDeletionsUseCase
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

class ProcessDeletionsUseCaseTest {

    private lateinit var repository: AppointmentRepository
    private lateinit var deleteAppointmentFromRemoteUseCase: DeleteAppointmentFromRemoteUseCase
    private lateinit var logger: Logger
    private lateinit var useCase: ProcessDeletionsUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        deleteAppointmentFromRemoteUseCase = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0])}
        every { logger.error(any()) } answers { println(it.invocation.args[0])}
        every { logger.warn(any()) } answers { println(it.invocation.args[0])}

        useCase = ProcessDeletionsUseCase(repository, deleteAppointmentFromRemoteUseCase, logger)
    }

    @Test
    @DisplayName("Should process deletions successfully when all deletions succeed")
    fun `should process deletions successfully when all deletions succeed`() = runTest {
        val deleteCache = mapOf(1 to true, 2 to true)

        coEvery { repository.getAppointmentsFromDeleteList() } returns Resource.Success(deleteCache)
        coEvery { deleteAppointmentFromRemoteUseCase.invoke(1, true) } returns Resource.Success(true)
        coEvery { deleteAppointmentFromRemoteUseCase.invoke(2, true) } returns Resource.Success(true)

        val result = useCase.invoke()

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        coVerify { repository.getAppointmentsFromDeleteList() }
        coVerify { deleteAppointmentFromRemoteUseCase.invoke(1, true) }
        coVerify { deleteAppointmentFromRemoteUseCase.invoke(2, true) }
        coVerify { repository.deleteAppointmentFromDeleteCache(1) }
        coVerify { repository.deleteAppointmentFromDeleteCache(2) }
        coVerify { logger.info("Processing deletions from cache") }
        coVerify { logger.info("Deleted appointment 1 successfully") }
        coVerify { logger.info("Deleted appointment 2 successfully") }
    }

    @Test
    @DisplayName("Should return Resource.Error when fetching delete cache fails")
    fun `should return Resource Error when fetching delete cache fails`() = runTest {
        val errorMessage = "Failed to fetch delete cache"

        coEvery { repository.getAppointmentsFromDeleteList() } returns Resource.Error(errorMessage)

        val result = useCase.invoke()

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, (result as Resource.Error).message)

        coVerify { repository.getAppointmentsFromDeleteList() }
        coVerify { logger.warn("Failed to get delete cache: $errorMessage") }
    }

    @Test
    @DisplayName("Should log warnings for failed deletions but continue processing")
    fun `should log warnings for failed deletions but continue processing`() = runTest {
        val deleteCache = mapOf(1 to true, 2 to true)

        coEvery { repository.getAppointmentsFromDeleteList() } returns Resource.Success(deleteCache)
        coEvery { deleteAppointmentFromRemoteUseCase.invoke(1, true) } returns Resource.Success(true)
        coEvery { deleteAppointmentFromRemoteUseCase.invoke(2, true) } returns Resource.Error("Failed to delete")

        val result = useCase.invoke()

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        coVerify { repository.getAppointmentsFromDeleteList() }
        coVerify { deleteAppointmentFromRemoteUseCase.invoke(1, true) }
        coVerify { deleteAppointmentFromRemoteUseCase.invoke(2, true) }
        coVerify { repository.deleteAppointmentFromDeleteCache(1) }
        coVerify(exactly = 0) { repository.deleteAppointmentFromDeleteCache(2) }
        coVerify { logger.info("Deleted appointment 1 successfully") }
        coVerify { logger.warn("Failed to delete appointment 2") }
    }
}
