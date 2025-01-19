import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.UpdateAppointmentInRemoteUseCase
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

class UpdateAppointmentInRemoteUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var logger: Logger
    private lateinit var useCase: UpdateAppointmentInRemoteUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0])}
        every { logger.error(any()) } answers { println(it.invocation.args[0])}
        every { logger.warn(any()) } answers { println(it.invocation.args[0])}

        useCase = UpdateAppointmentInRemoteUseCase(repository, logger)
    }

    @Test
    @DisplayName("Should return Resource.Success when appointment is updated successfully")
    fun shouldReturnResourceSuccessWhenAppointmentUpdatedSuccessfully() = runTest {
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
        val updatedAppointment = appointment.copy(isSynced = true, hasBeenSynced = true)
        coEvery { repository.updateRemoteAppointment(updatedAppointment) } returns Resource.Success(true)

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        coVerify { logger.info("Updating appointment in remote") }
        coVerify { logger.info("Appointment successfully updated in remote") }
        coVerify { repository.updateRemoteAppointment(updatedAppointment) }
    }

    @Test
    @DisplayName("Should return Resource.Error when updating appointment fails")
    fun shouldReturnResourceErrorWhenUpdatingAppointmentFails() = runTest {
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
        val updatedAppointment = appointment.copy(isSynced = true, hasBeenSynced = true)
        coEvery { repository.updateRemoteAppointment(updatedAppointment) } returns Resource.Error("Failed to update appointment in remote")

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Error)
        assertEquals("Failed to update appointment in remote", (result as Resource.Error).message)

        coVerify { logger.info("Updating appointment in remote") }
        coVerify { logger.error("Failed to update appointment in remote") }
        coVerify { repository.updateRemoteAppointment(updatedAppointment) }
    }
}
