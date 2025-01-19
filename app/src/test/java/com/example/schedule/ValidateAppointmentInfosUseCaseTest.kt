import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.ValidateAppointmentInfosUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import java.time.LocalDate
import java.time.LocalTime

class ValidateAppointmentInfosUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var logger: Logger
    private lateinit var useCase: ValidateAppointmentInfosUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0]) }
        every { logger.error(any()) } answers { println(it.invocation.args[0]) }
        every { logger.warn(any()) } answers { println(it.invocation.args[0]) }

        useCase = ValidateAppointmentInfosUseCase(repository, logger)
    }

    @Test
    @DisplayName("Should return Resource.Error when appointment title is empty")
    fun shouldReturnResourceErrorWhenAppointmentTitleIsEmpty() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "",
            notes = "Discuss project",
            color = 0xFFFFFF,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now()
        )

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Error)
        assertEquals("Appointment title cannot be empty", (result as Resource.Error).message)

        coVerify { logger.info("Validating appointment with title: ") }
        coVerify { logger.error("Appointment title cannot be empty") }
    }

    @Test
    @DisplayName("Should return Resource.Error when start date is after end date")
    fun shouldReturnResourceErrorWhenStartDateIsAfterEndDate() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Meeting",
            notes = "Discuss project",
            color = 0xFFFFFF,
            startDate = LocalDate.now().plusDays(1), // Start date after end date
            endDate = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now()
        )

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Error)
        assertEquals("Appointment start date must be before end date", (result as Resource.Error).message)

        coVerify { logger.info("Validating appointment with title: Meeting") }
        coVerify { logger.error("Appointment start date must be before end date") }
    }

    @Test
    @DisplayName("Should return Resource.Error when start time is after end time")
    fun shouldReturnResourceErrorWhenStartTimeIsAfterEndTime() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Meeting",
            notes = "Discuss project",
            color = 0xFFFFFF,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.now().plusHours(1), // Start time after end time
            endTime = LocalTime.now()
        )

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Error)
        assertEquals("Appointment start time must be before end time", (result as Resource.Error).message)

        coVerify { logger.info("Validating appointment with title: Meeting") }
        coVerify { logger.error("Appointment start time must be before end time") }
    }

    @Test
    @DisplayName("Should return Resource.Success when appointment is valid and ID is updated")
    fun shouldReturnResourceSuccessWhenAppointmentIsValidAndIdUpdated() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Meeting",
            notes = "Discuss project",
            color = 0xFFFFFF,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now()
        )

        val nextId = 1
        coEvery { repository.getLastIdInCache() } returns Resource.Success(0)

        val result = useCase.invoke(appointment)
        assertTrue(result is Resource.Success)

        val updatedAppointment = (result as Resource.Success).data
        assertNotNull(updatedAppointment)
        assertEquals(nextId, updatedAppointment?.id)

        coVerify { logger.info("Validating appointment with title: Meeting") }
        coVerify { logger.info("Fetching last ID from repository for new appointment") }
        coVerify { repository.getLastIdInCache() }
    }



    @Test
    @DisplayName("Should return Resource.Error when fetching last ID from repository fails")
    fun shouldReturnResourceErrorWhenFetchingLastIdFails() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Meeting",
            notes = "Discuss project",
            color = 0xFFFFFF,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now()
        )
        coEvery { repository.getLastIdInCache() } returns Resource.Error("Error fetching last ID")

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Error)
        assertEquals("Error fetching last ID", (result as Resource.Error).message)

        coVerify { logger.info("Validating appointment with title: Meeting") }
        coVerify { logger.error("Error fetching last ID: Error fetching last ID") }
    }
}
