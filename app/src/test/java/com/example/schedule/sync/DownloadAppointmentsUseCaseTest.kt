import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.AddAppointmentToCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.ClearRoomUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.remote.GetAppointmentsFromRemoteUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.sync.DownloadAppointmentsUseCase
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

class DownloadAppointmentsUseCaseTest {

    private lateinit var getAppointmentsFromRemoteUseCase: GetAppointmentsFromRemoteUseCase
    private lateinit var clearRoomUseCase: ClearRoomUseCase
    private lateinit var addAppointmentToCacheUseCase: AddAppointmentToCacheUseCase
    private lateinit var logger: Logger
    private lateinit var useCase: DownloadAppointmentsUseCase

    @BeforeEach
    fun setup() {
        getAppointmentsFromRemoteUseCase = mockk(relaxed = true)
        clearRoomUseCase = mockk(relaxed = true)
        addAppointmentToCacheUseCase = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0]) }
        every { logger.error(any()) } answers { println(it.invocation.args[0]) }
        every { logger.warn(any()) } answers { println(it.invocation.args[0]) }

        useCase = DownloadAppointmentsUseCase(
            getAppointmentsFromRemoteUseCase,
            clearRoomUseCase,
            addAppointmentToCacheUseCase,
            logger
        )
    }

    @Test
    @DisplayName("Should return Resource.Success when the download process completes successfully")
    fun `should return Resource Success when the download process completes successfully`() =
        runTest {
            val appointments = listOf(
                Appointment(
                    id = 1,
                    title = "Appointment 1",
                    notes = "Notes 1",
                    color = 0,
                    startDate = LocalDate.now(),
                    endDate = LocalDate.now(),
                    startTime = LocalTime.now(),
                    endTime = LocalTime.now().plusHours(2)
                ),
                Appointment(
                    id = 2,
                    title = "Appointment 2",
                    notes = "Notes 2",
                    color = 1,
                    startDate = LocalDate.now(),
                    endDate = LocalDate.now(),
                    startTime = LocalTime.now(),
                    endTime = LocalTime.now().plusHours(2)
                )
            )

            coEvery { getAppointmentsFromRemoteUseCase.invoke() } returns Resource.Success(
                appointments
            )
            coEvery { clearRoomUseCase.invoke() } returns Resource.Success(true)
            coEvery { addAppointmentToCacheUseCase.invoke(any()) } returns Resource.Success(true)

            val result = useCase.invoke()

            assertTrue(result is Resource.Success)
            assertEquals(true, (result as Resource.Success).data)

            coVerify { getAppointmentsFromRemoteUseCase.invoke() }
            coVerify { clearRoomUseCase.invoke() }
            coVerify(exactly = appointments.size) { addAppointmentToCacheUseCase.invoke(any()) }
            coVerify { logger.info("Starting data download process") }
            coVerify { logger.info("Fetching appointments from remote API") }
            coVerify { logger.info("Clearing local storage") }
            coVerify { logger.info("Data download process successfully completed") }
        }

    @Test
    @DisplayName("Should return Resource.Error when fetching appointments from remote API fails")
    fun `should return Resource Error when fetching appointments from remote API fails`() =
        runTest {
            val errorMessage = "Failed to fetch appointments from API"

            coEvery { getAppointmentsFromRemoteUseCase.invoke() } returns Resource.Error(
                errorMessage
            )

            val result = useCase.invoke()

            assertTrue(result is Resource.Error)
            assertEquals(
                "Failed to fetch appointments from API",
                (result as Resource.Error).message
            )

            coVerify { getAppointmentsFromRemoteUseCase.invoke() }
            coVerify { logger.error("Failed to fetch appointments from API: $errorMessage") }
        }

    @Test
    @DisplayName("Should return Resource.Error when clearing local storage fails")
    fun `should return Resource Error when clearing local storage fails`() = runTest {
        val appointments = listOf(
            Appointment(
                id = 1,
                title = "Appointment 1",
                notes = "Notes 1",
                color = 0,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now().plusHours(2)
            )
        )
        val errorMessage = "Failed to clear local storage"

        coEvery { getAppointmentsFromRemoteUseCase.invoke() } returns Resource.Success(appointments)
        coEvery { clearRoomUseCase.invoke() } returns Resource.Error(errorMessage)

        val result = useCase.invoke()

        assertTrue(result is Resource.Error)
        assertEquals("Failed to clear local storage", (result as Resource.Error).message)

        coVerify { getAppointmentsFromRemoteUseCase.invoke() }
        coVerify { clearRoomUseCase.invoke() }
        coVerify { logger.error("Failed to clear local storage: $errorMessage") }
    }

    @Test
    @DisplayName("Should log warnings when inserting appointments into cache fails")
    fun `should log warnings when inserting appointments into cache fails`() = runTest {
        val appointments = listOf(
            Appointment(
                id = 1,
                title = "Appointment 1",
                notes = "Notes 1",
                color = 0,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now().plusHours(2)
            ),
            Appointment(
                id = 2,
                title = "Appointment 2",
                notes = "Notes 2",
                color = 1,
                startDate = LocalDate.now(),
                endDate = LocalDate.now(),
                startTime = LocalTime.now(),
                endTime = LocalTime.now().plusHours(2)
            )
        )

        coEvery { getAppointmentsFromRemoteUseCase.invoke() } returns Resource.Success(appointments)
        coEvery { clearRoomUseCase.invoke() } returns Resource.Success(true)
        coEvery { addAppointmentToCacheUseCase.invoke(appointments[0]) } returns Resource.Success(
            true
        )
        coEvery { addAppointmentToCacheUseCase.invoke(appointments[1]) } returns Resource.Error("Failed to insert appointment with ID: 2")

        val result = useCase.invoke()

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        coVerify { getAppointmentsFromRemoteUseCase.invoke() }
        coVerify { clearRoomUseCase.invoke() }
        coVerify(exactly = 2) { addAppointmentToCacheUseCase.invoke(any()) }
        coVerify { logger.warn("Failed to insert appointment with ID: 2") }
    }
}
