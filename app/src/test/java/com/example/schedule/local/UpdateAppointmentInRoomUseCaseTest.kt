import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.ValidateAppointmentInfosUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpdateAppointmentInRoomUseCase
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

class UpdateAppointmentInRoomUseCaseTest {

    private lateinit var repository: AppointmentRepository
    private lateinit var validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase
    private lateinit var logger: Logger
    private lateinit var useCase: UpdateAppointmentInRoomUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        validateAppointmentInfosUseCase = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0])}
        every { logger.error(any()) } answers { println(it.invocation.args[0])}
        every { logger.warn(any()) } answers { println(it.invocation.args[0])}

        useCase = UpdateAppointmentInRoomUseCase(repository, validateAppointmentInfosUseCase, logger)
    }

    @Test
    @DisplayName("Should return Resource.Success when appointment is successfully updated")
    fun `should return Resource Success when appointment is successfully updated`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Valid Appointment",
            notes = "Update Test",
            color = 1,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
        )

        coEvery { validateAppointmentInfosUseCase.invoke(appointment) } returns Resource.Success(appointment)
        coEvery { repository.updateAppointmentInRoom(appointment) } returns Resource.Success(true)

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        coVerify { validateAppointmentInfosUseCase.invoke(appointment) }
        coVerify { repository.updateAppointmentInRoom(appointment) }
        coVerify { logger.info("Successfully updated appointment in room.") }
    }

    @Test
    @DisplayName("Should return Resource.Error when validation fails")
    fun `should return Resource Error when validation fails`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "",
            notes = "Invalid Appointment",
            color = 1,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
        )

        val validationErrorMessage = "Validation failed"
        coEvery { validateAppointmentInfosUseCase.invoke(appointment) } returns Resource.Error(validationErrorMessage)

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Error)
        assertEquals(validationErrorMessage, (result as Resource.Error).message)

        coVerify { validateAppointmentInfosUseCase.invoke(appointment) }
        coVerify(exactly = 0) { repository.updateAppointmentInRoom(any()) }
        coVerify { logger.error(validationErrorMessage) }
    }

    @Test
    @DisplayName("Should return Resource.Error when repository fails to update appointment")
    fun `should return Resource Error when repository fails to update appointment`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Valid Appointment",
            notes = "Update Test",
            color = 1,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(10, 0),
        )

        val repositoryErrorMessage = "Failed to update appointment in Room"
        coEvery { validateAppointmentInfosUseCase.invoke(appointment) } returns Resource.Success(appointment)
        coEvery { repository.updateAppointmentInRoom(appointment) } returns Resource.Error(repositoryErrorMessage)

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Error)
        assertEquals(repositoryErrorMessage, (result as Resource.Error).message)

        coVerify { validateAppointmentInfosUseCase.invoke(appointment) }
        coVerify { repository.updateAppointmentInRoom(appointment) }
        coVerify { logger.error(repositoryErrorMessage) }
    }
}
