import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetDatesFromCacheByAppointmentUseCase
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.slf4j.Logger

class GetDatesFromCacheByAppointmentUseCaseTest {

    private lateinit var repository: AppointmentRepository
    private lateinit var logger: Logger
    private lateinit var useCase: GetDatesFromCacheByAppointmentUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0])}
        every { logger.error(any()) } answers { println(it.invocation.args[0])}
        every { logger.warn(any()) } answers { println(it.invocation.args[0])}

        useCase = GetDatesFromCacheByAppointmentUseCase(repository, logger)
    }

    @Test
    @DisplayName("Should return Resource.Success with list of dates when data is found")
    fun `should return Resource Success with list of dates when data is found`() = runTest {
        val appointmentId = 1
        val dates = listOf(20230101, 20230102)

        coEvery { repository.getDatesByAppointment(appointmentId) } returns Resource.Success(dates)

        val result = useCase.invoke(appointmentId)
        assertTrue(result is Resource.Success)
        assertEquals(dates, (result as Resource.Success).data)

        verify { logger.info("Fetching dates from cache for appointment ID: $appointmentId") }
        coVerify { repository.getDatesByAppointment(appointmentId) }
    }

    @Test
    @DisplayName("Should return Resource.Error when no data is found")
    fun `should return Resource Error when no data is found`() = runTest {
        val appointmentId = 1

        coEvery { repository.getDatesByAppointment(appointmentId) } returns
                Resource.Error("No dates found for appointment ID: $appointmentId")

        val result = useCase.invoke(appointmentId)
        assertTrue(result is Resource.Error)
        assertEquals("No dates found for appointment ID: $appointmentId", (result as Resource.Error).message)

        verify { logger.info("Fetching dates from cache for appointment ID: $appointmentId") }
        coVerify { repository.getDatesByAppointment(appointmentId) }
    }

    @Test
    @DisplayName("Should return Resource.Error when repository throws an exception")
    fun `should return Resource Error when repository throws an exception`() = runTest {
        val appointmentId = 1

        coEvery { repository.getDatesByAppointment(appointmentId) } returns Resource.Error("Unknown Error")

        val result = useCase.invoke(appointmentId)
        assertTrue(result is Resource.Error)
        assertEquals("Unknown Error", (result as Resource.Error).message)

        verify { logger.info("Fetching dates from cache for appointment ID: $appointmentId") }
        coVerify { repository.getDatesByAppointment(appointmentId) }
    }
}
