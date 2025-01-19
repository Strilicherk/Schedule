import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.GetAllAppointmentsFromRoomUseCase
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

class GetAllAppointmentsFromRoomUseCaseTest {

    private lateinit var repository: AppointmentRepository
    private lateinit var logger: Logger
    private lateinit var useCase: GetAllAppointmentsFromRoomUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0])}
        every { logger.error(any()) } answers { println(it.invocation.args[0])}
        every { logger.warn(any()) } answers { println(it.invocation.args[0])}

        useCase = GetAllAppointmentsFromRoomUseCase(repository, logger)
    }

    @Test
    @DisplayName("Should return Resource.Success with appointments list when fetching succeeds")
    fun `should return Resource Success with appointments list when fetching succeeds`() = runTest {
        val appointments = listOf(
            Appointment(
                id = 1,
                title = "Appointment 1",
                notes = "Notes 1",
                color = 1,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.of(9, 0),
                endTime = LocalTime.of(10, 0)
            ),
            Appointment(
                id = 2,
                title = "Appointment 2",
                notes = "Notes 2",
                color = 2,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.of(11, 0),
                endTime = LocalTime.of(12, 0)
            )
        )

        coEvery { repository.getAllAppointmentsFromRoom() } returns Resource.Success(appointments)

        val result = useCase.invoke()

        assertTrue(result is Resource.Success)
        assertEquals(appointments, (result as Resource.Success).data)

        coVerify { repository.getAllAppointmentsFromRoom() }
        coVerify { logger.info("Fetching all appointments from Room") }
        coVerify { logger.info("Appointments successfully fetched") }
    }

    @Test
    @DisplayName("Should return Resource.Error when fetching appointments fails")
    fun `should return Resource Error when fetching appointments fails`() = runTest {
        val errorMessage = "Failed to fetch appointments"

        coEvery { repository.getAllAppointmentsFromRoom() } returns Resource.Error(errorMessage)

        val result = useCase.invoke()

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, (result as Resource.Error).message)

        coVerify { repository.getAllAppointmentsFromRoom() }
        coVerify { logger.info("Fetching all appointments from Room") }
        coVerify { logger.error(errorMessage) }
    }
}
