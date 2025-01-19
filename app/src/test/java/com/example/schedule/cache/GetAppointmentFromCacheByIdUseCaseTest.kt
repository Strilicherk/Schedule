import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetAppointmentFromCacheByIdUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import java.time.LocalDate
import java.time.LocalTime

class GetAppointmentFromCacheByIdUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var logger: Logger
    private lateinit var useCase: GetAppointmentFromCacheByIdUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0]) }
        every { logger.warn(any()) } answers { println(it.invocation.args[0]) }
        every { logger.error(any()) } answers { println(it.invocation.args[0]) }

        useCase = GetAppointmentFromCacheByIdUseCase(repository, logger)
    }

    @Test
    @DisplayName("Should return Resource.Success when appointment is found in cache")
    fun `should return Resource Success when appointment is found in cache`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Test Appointment",
            notes = "Test Notes",
            color = 0,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now()
        )

        coEvery { repository.getAppointmentById(1) } returns Resource.Success(appointment)

        val result = useCase.invoke(1)

        assertTrue(result is Resource.Success)
        assertEquals(appointment, (result as Resource.Success).data)

        coVerify { repository.getAppointmentById(1) }
        verify { logger.info("Fetching appointment from cache by ID: 1") }
    }

    @Test
    @DisplayName("Should return Resource.Error when appointment is not found in cache")
    fun `should return Resource Error when appointment is not found in cache`() = runTest {
        coEvery { repository.getAppointmentById(1) } returns Resource.Error("Appointment not found")

        val result = useCase.invoke(1)

        assertTrue(result is Resource.Error)
        assertEquals("Appointment not found", (result as Resource.Error).message)

        coVerify { repository.getAppointmentById(1) }
        verify { logger.info("Fetching appointment from cache by ID: 1") }
    }

    @Test
    @DisplayName("Should return Resource.Error when repository throws an exception")
    fun `should return Resource Error when repository throws an exception`() = runTest {
        coEvery { repository.getAppointmentById(1) } returns Resource.Error("IllegalArgumentException: Unknown Error")

        val result = useCase.invoke(1)

        assertTrue(result is Resource.Error)
        assertEquals("IllegalArgumentException: Unknown Error", (result as Resource.Error).message)

        coVerify { repository.getAppointmentById(1) }
        verify { logger.info("Fetching appointment from cache by ID: 1") }
    }
}
