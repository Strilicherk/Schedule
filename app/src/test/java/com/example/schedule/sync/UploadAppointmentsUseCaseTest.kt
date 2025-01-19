import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetAppointmentsFromRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.sync.ProcessDeletionsUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.sync.UploadAppointmentsUseCase
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
import java.time.LocalTime

class UploadAppointmentsUseCaseTest {

    private lateinit var repository: AppointmentRepository
    private lateinit var getAppointmentsFromRemoteUseCase: GetAppointmentsFromRemoteUseCase
    private lateinit var upsertUnsyncedAppointmentsToRemoteUseCase: UpsertUnsyncedAppointmentsToRemoteUseCase
    private lateinit var processDeletionsUseCase: ProcessDeletionsUseCase
    private lateinit var logger: Logger
    private lateinit var useCase: UploadAppointmentsUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        getAppointmentsFromRemoteUseCase = mockk(relaxed = true)
        upsertUnsyncedAppointmentsToRemoteUseCase = mockk(relaxed = true)
        processDeletionsUseCase = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0]) }
        every { logger.error(any()) } answers { println(it.invocation.args[0]) }
        every { logger.warn(any()) } answers { println(it.invocation.args[0]) }

        useCase = UploadAppointmentsUseCase(
            repository,
            getAppointmentsFromRemoteUseCase,
            upsertUnsyncedAppointmentsToRemoteUseCase,
            processDeletionsUseCase,
            logger
        )
    }

    @Test
    @DisplayName("Should complete synchronization successfully")
    fun `should complete synchronization successfully`() = runTest {
        val remoteAppointments = listOf(
            Appointment(
                id = 1, title = "Remote Appointment 1", notes = "Notes 1", color = 0,
                startDate = LocalDate.now(), endDate = LocalDate.now(), startTime = LocalTime.now(),
                endTime = LocalTime.now().plusHours(2)
            ),
            Appointment(
                id = 2, title = "Remote Appointment 2", notes = "Notes 1", color = 0,
                startDate = LocalDate.now(), endDate = LocalDate.now(), startTime = LocalTime.now(),
                endTime = LocalTime.now().plusHours(2)
            )
        )
        val unsyncedAppointments = listOf(
            Appointment(
                id = 3, title = "Unsynced Appointment 1", notes = "Notes 1", color = 0,
                startDate = LocalDate.now(), endDate = LocalDate.now(), startTime = LocalTime.now(),
                endTime = LocalTime.now().plusHours(2)
            ),
            Appointment(
                id = 4, title = "Unsynced Appointment 2", notes = "Notes 1", color = 0,
                startDate = LocalDate.now(), endDate = LocalDate.now(), startTime = LocalTime.now(),
                endTime = LocalTime.now().plusHours(2)
            )
        )

        coEvery { processDeletionsUseCase.invoke() } returns Resource.Success(true)
        coEvery { getAppointmentsFromRemoteUseCase.invoke() } returns Resource.Success(
            remoteAppointments
        )
        coEvery { repository.getUnsyncedAppointmentsFromRoom() } returns Resource.Success(
            unsyncedAppointments
        )
        coEvery {
            upsertUnsyncedAppointmentsToRemoteUseCase.invoke(
                unsyncedAppointments,
                remoteAppointments
            )
        } returns Resource.Success(true)

        val result = useCase.invoke()

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        coVerify { processDeletionsUseCase.invoke() }
        coVerify { getAppointmentsFromRemoteUseCase.invoke() }
        coVerify { repository.getUnsyncedAppointmentsFromRoom() }
        coVerify {
            upsertUnsyncedAppointmentsToRemoteUseCase.invoke(
                unsyncedAppointments,
                remoteAppointments
            )
        }
        coVerify { logger.info("Synchronization successfully completed") }
    }

    @Test
    @DisplayName("Should return Resource.Error when fetching appointments from API fails")
    fun `should return Resource Error when fetching appointments from API fails`() = runTest {
        val errorMessage = "Failed to fetch appointments from API"

        coEvery { processDeletionsUseCase.invoke() } returns Resource.Success(true)
        coEvery { getAppointmentsFromRemoteUseCase.invoke() } returns Resource.Error(errorMessage)

        val result = useCase.invoke()

        assertTrue(result is Resource.Error)
        assertEquals("Failed to fetch appointments from API", (result as Resource.Error).message)

        coVerify { processDeletionsUseCase.invoke() }
        coVerify { getAppointmentsFromRemoteUseCase.invoke() }
        coVerify { logger.error("Failed to fetch appointments from API: $errorMessage") }
    }

    @Test
    @DisplayName("Should return Resource.Error when retrieving unsynced appointments fails")
    fun `should return Resource Error when retrieving unsynced appointments fails`() = runTest {
        val remoteAppointments = listOf(
            Appointment(
                id = 1, title = "Remote Appointment", notes = "Notes 1", color = 0,
                startDate = LocalDate.now(), endDate = LocalDate.now(), startTime = LocalTime.now(),
                endTime = LocalTime.now().plusHours(2)
            )
        )
        val errorMessage = "Failed to retrieve unsynced appointments"

        coEvery { processDeletionsUseCase.invoke() } returns Resource.Success(true)
        coEvery { getAppointmentsFromRemoteUseCase.invoke() } returns Resource.Success(
            remoteAppointments
        )
        coEvery { repository.getUnsyncedAppointmentsFromRoom() } returns Resource.Error(errorMessage)

        val result = useCase.invoke()

        assertTrue(result is Resource.Error)
        assertEquals("Failed to retrieve unsynced appointments", (result as Resource.Error).message)

        coVerify { processDeletionsUseCase.invoke() }
        coVerify { getAppointmentsFromRemoteUseCase.invoke() }
        coVerify { repository.getUnsyncedAppointmentsFromRoom() }
        coVerify { logger.error("Failed to retrieve unsynced appointments: $errorMessage") }
    }

    @Test
    @DisplayName("Should log info when no unsynced appointments are found")
    fun `should log info when no unsynced appointments are found`() = runTest {
        val remoteAppointments = listOf(
            Appointment(
                id = 1, title = "Remote Appointment", notes = "Notes 1", color = 0,
                startDate = LocalDate.now(), endDate = LocalDate.now(), startTime = LocalTime.now(),
                endTime = LocalTime.now().plusHours(2)
            )
        )

        coEvery { processDeletionsUseCase.invoke() } returns Resource.Success(true)
        coEvery { getAppointmentsFromRemoteUseCase.invoke() } returns Resource.Success(
            remoteAppointments
        )
        coEvery { repository.getUnsyncedAppointmentsFromRoom() } returns Resource.Success(emptyList())

        val result = useCase.invoke()

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        coVerify { processDeletionsUseCase.invoke() }
        coVerify { getAppointmentsFromRemoteUseCase.invoke() }
        coVerify { repository.getUnsyncedAppointmentsFromRoom() }
        coVerify { logger.info("No unsynced appointments found") }
    }
}
