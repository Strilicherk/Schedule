import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.AddAppointmentToCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.DeleteAppointmentFromCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.DeleteAppointmentFromRoomUseCase
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.slf4j.Logger
import java.time.LocalDate
import java.time.LocalTime

class DeleteAppointmentFromCacheUseCaseTest {

    private lateinit var repository: AppointmentRepository
    private lateinit var deleteAppointmentFromRoomUseCase: DeleteAppointmentFromRoomUseCase
    private lateinit var addAppointmentToCacheUseCase: AddAppointmentToCacheUseCase
    private lateinit var logger: Logger
    private lateinit var useCase: DeleteAppointmentFromCacheUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        deleteAppointmentFromRoomUseCase = mockk(relaxed = true)
        addAppointmentToCacheUseCase = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0]) }
        every { logger.warn(any()) } answers { println(it.invocation.args[0]) }
        every { logger.error(any()) } answers { println(it.invocation.args[0]) }
        useCase = DeleteAppointmentFromCacheUseCase(
            repository,
            deleteAppointmentFromRoomUseCase,
            addAppointmentToCacheUseCase,
            logger
        )
    }

    @Test
    @DisplayName("Should return Resource.Success when appointment is successfully deleted from cache")
    fun `should return Resource Success when appointment is successfully deleted from cache`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Test Appointment",
            notes = "Test Notes",
            color = 0,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now(),
            hasBeenSynced = true
        )
        val dates = listOf(20230101, 20230102)

        coEvery { repository.getDatesByAppointment(appointment.id) } returns Resource.Success(dates)
        coEvery { deleteAppointmentFromRoomUseCase.invoke(appointment.id) } returns Resource.Success(true)
        coEvery { repository.deleteAppointmentFromByDateCache(any(), appointment.id) } returns Resource.Success(true)
        coEvery { repository.deleteAppointmentFromCache(appointment) } returns Resource.Success(true)
        coEvery { repository.addAppointmentToDeleteCache(appointment.id, appointment.hasBeenSynced) } returns Resource.Success(true)

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        coVerify { repository.getDatesByAppointment(appointment.id) }
        coVerify { deleteAppointmentFromRoomUseCase.invoke(appointment.id) }
        coVerify { repository.deleteAppointmentFromByDateCache(20230101, appointment.id) }
        coVerify { repository.deleteAppointmentFromByDateCache(20230102, appointment.id) }
        coVerify { repository.deleteAppointmentFromCache(appointment) }
        coVerify { repository.addAppointmentToDeleteCache(appointment.id, appointment.hasBeenSynced) }
        verify { logger.info("Deleting appointment from cache.") }
        verify { logger.info("Appointment successfully deleted from cache.") }
    }

    @Test
    @DisplayName("Should return Resource.Error when no dates are found for the appointment")
    fun `should return Resource Error when no dates are found for the appointment`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Test Appointment",
            notes = "Test Notes",
            color = 0,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now(),
            hasBeenSynced = true
        )

        coEvery { repository.getDatesByAppointment(appointment.id) } returns Resource.Success(emptyList())

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Error)
        assertEquals("No dates found for appointment ID: 1", (result as Resource.Error).message)

        coVerify { repository.getDatesByAppointment(appointment.id) }
        verify { logger.info("Deleting appointment from cache.") }
    }

    @Test
    @DisplayName("Should return Resource.Error when deleting from Room fails")
    fun `should return Resource Error when deleting from Room fails`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Test Appointment",
            notes = "Test Notes",
            color = 0,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now(),
            hasBeenSynced = true
        )
        val dates = listOf(20230101, 20230102)

        coEvery { repository.getDatesByAppointment(appointment.id) } returns Resource.Success(dates)
        coEvery { deleteAppointmentFromRoomUseCase.invoke(appointment.id) } returns Resource.Error("Failed to delete from Room")

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Error)
        assertEquals("Failed to delete from Room", (result as Resource.Error).message)

        coVerify { repository.getDatesByAppointment(appointment.id) }
        coVerify { deleteAppointmentFromRoomUseCase.invoke(appointment.id) }
        verify { logger.info("Deleting appointment from cache.") }
    }

    @Test
    @DisplayName("Should return Resource.Error and re-add appointment to cache when deletion fails")
    fun `should return Resource Error and re-add appointment to cache when deletion fails`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Test Appointment",
            notes = "Test Notes",
            color = 0,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now(),
            hasBeenSynced = true
        )
        val dates = listOf(20230101, 20230102)

        coEvery { repository.getDatesByAppointment(appointment.id) } returns Resource.Success(dates)
        coEvery { deleteAppointmentFromRoomUseCase.invoke(appointment.id) } returns Resource.Success(true)
        coEvery { repository.deleteAppointmentFromByDateCache(any(), appointment.id) } throws RuntimeException("Cache error")
        coEvery { addAppointmentToCacheUseCase.invoke(appointment) } returns Resource.Success(true)

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Error)
        assertEquals("Failed to delete appointment from cache: Cache error", (result as Resource.Error).message)

        coVerify { repository.getDatesByAppointment(appointment.id) }
        coVerify { deleteAppointmentFromRoomUseCase.invoke(appointment.id) }
        coVerify { repository.deleteAppointmentFromByDateCache(20230101, appointment.id) }
        coVerify { addAppointmentToCacheUseCase.invoke(appointment) }
        verify { logger.info("Deleting appointment from cache.") }
        verify { logger.error("Failed to delete appointment from cache: Cache error") }
    }
}
