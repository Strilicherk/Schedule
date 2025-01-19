import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.AddAppointmentToRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.UpdateAppointmentInRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpdateAppointmentInRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.sync.UpsertUnsyncedAppointmentsToRemoteUseCase
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
import java.time.LocalDateTime
import java.time.LocalTime

class UpsertUnsyncedAppointmentsToRemoteUseCaseTest {

    private lateinit var updateAppointmentInRemoteUseCase: UpdateAppointmentInRemoteUseCase
    private lateinit var updateAppointmentInRoomUseCase: UpdateAppointmentInRoomUseCase
    private lateinit var addAppointmentToRemoteUseCase: AddAppointmentToRemoteUseCase
    private lateinit var logger: Logger
    private lateinit var useCase: UpsertUnsyncedAppointmentsToRemoteUseCase

    @BeforeEach
    fun setup() {
        updateAppointmentInRemoteUseCase = mockk(relaxed = true)
        updateAppointmentInRoomUseCase = mockk(relaxed = true)
        addAppointmentToRemoteUseCase = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0]) }
        every { logger.error(any()) } answers { println(it.invocation.args[0]) }
        every { logger.warn(any()) } answers { println(it.invocation.args[0]) }

        useCase = UpsertUnsyncedAppointmentsToRemoteUseCase(
            updateAppointmentInRemoteUseCase,
            updateAppointmentInRoomUseCase,
            addAppointmentToRemoteUseCase,
            logger
        )
    }

    @Test
    @DisplayName("Should update synced appointments and create new ones successfully")
    fun `should update synced appointments and create new ones successfully`() = runTest {
        val unsyncedAppointments = listOf(
            Appointment(
                id = 1,
                title = "Synced Appointment",
                hasBeenSynced = true,
                lastModified = LocalDateTime.now().plusDays(1),
                notes = "Notes 1",
                color = 0,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now().plusHours(2)
            ),
            Appointment(
                id = 2,
                title = "Unsynced Appointment",
                notes = "Notes 1",
                color = 0,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now().plusHours(2),
                hasBeenSynced = false
            )
        )
        val remoteAppointments = listOf(
            Appointment(
                id = 1,
                title = "Old Appointment",
                notes = "Notes 1",
                color = 0,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now().plusHours(2),
                lastModified = LocalDateTime.now()
            )
        )

        coEvery { updateAppointmentInRemoteUseCase.invoke(any()) } returns Resource.Success(true)
        coEvery { updateAppointmentInRoomUseCase.invoke(any()) } returns Resource.Success(true)
        coEvery { addAppointmentToRemoteUseCase.invoke(any()) } returns Resource.Success(true)

        val result = useCase.invoke(unsyncedAppointments, remoteAppointments)

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        coVerify { updateAppointmentInRemoteUseCase.invoke(unsyncedAppointments[0]) }
        coVerify { updateAppointmentInRoomUseCase.invoke(unsyncedAppointments[0].copy(isSynced = true)) }
        coVerify { addAppointmentToRemoteUseCase.invoke(unsyncedAppointments[1]) }
        coVerify { logger.info("Successfully updated appointment 1") }
        coVerify { logger.info("Successfully created appointment with ID 2") }
    }

    @Test
    @DisplayName("Should skip update for already up-to-date appointments")
    fun `should skip update for already up-to-date appointments`() = runTest {
        val unsyncedAppointments = listOf(
            Appointment(
                id = 1,
                title = "Synced Appointment",
                hasBeenSynced = true,
                lastModified = LocalDateTime.now(),
                notes = "Notes 1",
                color = 0,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now().plusHours(2)
            )
        )
        val remoteAppointments = listOf(
            Appointment(
                id = 1, title = "Remote Appointment", hasBeenSynced = true,
                lastModified = LocalDateTime.now().plusDays(1),
                notes = "Notes 1",
                color = 0,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now().plusHours(2)
            )
        )

        val result = useCase.invoke(unsyncedAppointments, remoteAppointments)

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        coVerify(exactly = 0) { updateAppointmentInRemoteUseCase.invoke(any()) }
        coVerify { logger.info("Skipping update for appointment 1 as it is already up to date.") }
    }

    @Test
    @DisplayName("Should return Resource.Error when updating synced appointment fails")
    fun `should return Resource Error when updating synced appointment fails`() = runTest {
        val unsyncedAppointments = listOf(
            Appointment(
                id = 1,
                title = "Synced Appointment",
                hasBeenSynced = true,
                lastModified = LocalDateTime.now().plusDays(1),
                notes = "Notes 1",
                color = 0,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now().plusHours(2)
            )
        )
        val remoteAppointments = listOf(
            Appointment(
                id = 1, title = "Remote Appointment",
                lastModified = LocalDateTime.now(),
                notes = "Notes 1",
                color = 0,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now().plusHours(2)
            )
        )

        coEvery { updateAppointmentInRemoteUseCase.invoke(any()) } returns Resource.Error("Failed to update")

        val result = useCase.invoke(unsyncedAppointments, remoteAppointments)

        assertTrue(result is Resource.Success) // Atualizações individuais não interrompem o fluxo principal
        coVerify { logger.warn("Failed to update appointment 1") }
    }

    @Test
    @DisplayName("Should return Resource.Error when creating new appointment fails")
    fun `should return Resource Error when creating new appointment fails`() = runTest {
        val unsyncedAppointments = listOf(
            Appointment(
                id = 2, title = "Unsynced Appointment", hasBeenSynced = false,
                notes = "Notes 1",
                color = 0,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now().plusHours(2)
            )
        )

        coEvery { addAppointmentToRemoteUseCase.invoke(any()) } returns Resource.Error("Failed to create")

        val result = useCase.invoke(unsyncedAppointments, emptyList())

        assertTrue(result is Resource.Error)
        assertEquals("Failed to create", (result as Resource.Error).message)

        coVerify { addAppointmentToRemoteUseCase.invoke(any()) }
        coVerify { logger.warn("Failed to create appointment 2") }
    }
}
