import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetAppointmentsFromCacheByDateUseCase
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

class GetAppointmentsFromCacheByDateUseCaseTest {

    private lateinit var repository: AppointmentRepository
    private lateinit var logger: Logger
    private lateinit var useCase: GetAppointmentsFromCacheByDateUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0])}
        every { logger.error(any()) } answers { println(it.invocation.args[0])}
        every { logger.warn(any()) } answers { println(it.invocation.args[0])}
        useCase = GetAppointmentsFromCacheByDateUseCase(repository, logger)
    }

    @Test
    @DisplayName("Should return Resource.Success with empty list when no appointments are found for the given date")
    fun `should return Resource Success with empty list when no appointments are found for the given date`() = runTest {
        coEvery { repository.getAppointmentListByDate(1) } returns Resource.Success(emptyList())

        val result = useCase.invoke(1)

        assertTrue(result is Resource.Success)
        assertEquals(emptyList<Appointment>(), (result as Resource.Success).data)

        coVerify { repository.getAppointmentListByDate(1) }
        verify { logger.info("Fetching appointments from cache by date: 1") }
        verify { logger.info("No appointments found for date: 1") }
    }

    @Test
    @DisplayName("Should return Resource.Success with appointment list when appointments are found for the given date")
    fun `should return Resource Success with appointment list when appointments are found for the given date`() = runTest {
        val appointmentIds = listOf(1, 2)
        val appointments = listOf(
            Appointment(
                id = 1,
                title = "Test Appointment 1",
                notes = "Notes 1",
                color = 0,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now()
            ),
            Appointment(
                id = 2,
                title = "Test Appointment 2",
                notes = "Notes 2",
                color = 0,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now()
            )
        )

        coEvery { repository.getAppointmentListByDate(1) } returns Resource.Success(appointmentIds)
        coEvery { repository.getAppointmentById(1) } returns Resource.Success(appointments[0])
        coEvery { repository.getAppointmentById(2) } returns Resource.Success(appointments[1])

        val result = useCase.invoke(1)

        assertTrue(result is Resource.Success)
        assertEquals(appointments, (result as Resource.Success).data)

        coVerify { repository.getAppointmentListByDate(1) }
        coVerify { repository.getAppointmentById(1) }
        coVerify { repository.getAppointmentById(2) }
        verify { logger.info("Fetching appointments from cache by date: 1") }
        verify { logger.info("Appointments fetched successfully.") }
    }

    @Test
    @DisplayName("Should return Resource.Error when fetching appointment list by date fails")
    fun `should return Resource Error when fetching appointment list by date fails`() = runTest {
        coEvery { repository.getAppointmentListByDate(1) } returns Resource.Error("Failed to fetch appointment list")

        val result = useCase.invoke(1)
        assertTrue(result is Resource.Error)
        assertEquals("Failed to fetch appointment list", (result as Resource.Error).message)

        coVerify { repository.getAppointmentListByDate(1) }
        verify { logger.info("Fetching appointments from cache by date: 1") }
    }

    @Test
    @DisplayName("Should return Resource.Error when fetching individual appointments fails")
    fun `should return Resource Error when fetching individual appointments fails`() = runTest {
        val appointmentIds = listOf(1, 2)

        coEvery { repository.getAppointmentListByDate(1) } returns Resource.Success(appointmentIds)
        coEvery { repository.getAppointmentById(1) } returns Resource.Success(
            Appointment(
                id = 1,
                title = "Test Appointment 1",
                notes = "Notes 1",
                color = 0,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now()
            )
        )
        coEvery { repository.getAppointmentById(2) } returns Resource.Error("Failed to fetch appointment with ID 2")

        val result = useCase.invoke(1)

        assertTrue(result is Resource.Error)
        assertEquals("Failed to fetch appointment with ID 2", (result as Resource.Error).message)

        coVerify { repository.getAppointmentListByDate(1) }
        coVerify { repository.getAppointmentById(1) }
        coVerify { repository.getAppointmentById(2) }
        verify { logger.info("Fetching appointments from cache by date: 1") }
    }
}
