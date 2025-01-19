import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetAppointmentsFromRemoteUseCase
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
import java.time.LocalDate
import java.time.LocalTime

class GetAppointmentsFromRemoteUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var logger: Logger
    private lateinit var useCase: GetAppointmentsFromRemoteUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0])}
        every { logger.error(any()) } answers { println(it.invocation.args[0])}
        every { logger.warn(any()) } answers { println(it.invocation.args[0])}

        useCase = GetAppointmentsFromRemoteUseCase(repository, logger)
    }

    @Test
    @DisplayName("Should return Resource.Success when appointments are fetched successfully")
    fun shouldReturnResourceSuccessWhenAppointmentsFetchedSuccessfully() = runTest {
        val appointments = listOf(
            Appointment(
                id = 1,
                title = "Meeting",
                notes = "Discuss project",
                color = 0xFFFFFF,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now()
            )
        )
        coEvery { repository.getRemoteAppointments() } returns Resource.Success(appointments)

        val result = useCase.invoke()

        assertTrue(result is Resource.Success)
        assertEquals(appointments, (result as Resource.Success).data)

        coVerify { logger.info("Fetching appointments from remote") }
        coVerify { logger.info("Appointment list success fetched from remote") }
        coVerify { repository.getRemoteAppointments() }
    }

    @Test
    @DisplayName("Should return Resource.Error when fetching appointments fails")
    fun shouldReturnResourceErrorWhenFetchingAppointmentsFails() = runTest {
        coEvery { repository.getRemoteAppointments() } returns Resource.Error("Failed to fetch appointments from remote")

        val result = useCase.invoke()

        assertTrue(result is Resource.Error)
        assertEquals("Failed to fetch appointments from remote", (result as Resource.Error).message)

        coVerify { logger.info("Fetching appointments from remote") }
        coVerify { logger.error("Failed to fetch appointment list from remote") }
        coVerify { repository.getRemoteAppointments() }
    }
}
