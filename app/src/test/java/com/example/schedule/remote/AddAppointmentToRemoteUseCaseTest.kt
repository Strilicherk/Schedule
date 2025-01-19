import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.AddAppointmentToRemoteUseCase
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

class AddAppointmentToRemoteUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var logger: Logger
    private lateinit var useCase: AddAppointmentToRemoteUseCase

    private val appointment = Appointment(
        id = 1,
        title = "Doctor's Appointment",
        notes = "Annual check-up",
        color = 0xFF0000,
        startDate = LocalDate.now(),
        endDate = LocalDate.now().plusDays(1),
        startTime = LocalTime.of(10, 0),
        endTime = LocalTime.of(11, 0)
    )

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0])}
        every { logger.error(any()) } answers { println(it.invocation.args[0])}
        every { logger.warn(any()) } answers { println(it.invocation.args[0])}

        useCase = AddAppointmentToRemoteUseCase(repository, logger)
    }

    @Test
    @DisplayName("Should return Resource.Success when adding appointment to remote is successful")
    fun `should return Resource Success when adding appointment to remote is successful`() = runTest {
        coEvery { repository.addAppointmentToRemote(appointment) } returns Resource.Success(true)

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        coVerify { repository.addAppointmentToRemote(appointment) }
        coVerify { logger.info("Adding appointment to remote") }
        coVerify { logger.info("Appointment success added to remote") }
    }

    @Test
    @DisplayName("Should return Resource.Error when adding appointment to remote fails")
    fun `should return Resource Error when adding appointment to remote fails`() = runTest {
        val errorMessage = "Failed to add appointment to remote"
        coEvery { repository.addAppointmentToRemote(appointment) } returns Resource.Error(errorMessage)

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Error)
        assertEquals(errorMessage, (result as Resource.Error).message)

        coVerify { repository.addAppointmentToRemote(appointment) }
        coVerify { logger.info("Adding appointment to remote") }
        coVerify { logger.error("Failed to add appointment in remote") }
    }
}
