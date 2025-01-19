import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.ValidateAppointmentInfosUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.AddAppointmentToCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetAppointmentFromCacheByIdUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.GetDatesFromCacheByAppointmentUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.UpdateAppointmentInCacheUseCase
import com.example.schedule.feature_schedule.domain.use_case.appointment.local.UpdateAppointmentInRoomUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import java.time.LocalDate
import java.time.LocalTime

class UpdateAppointmentInCacheUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var addAppointmentToCacheUseCase: AddAppointmentToCacheUseCase
    private lateinit var updateAppointmentInRoomUseCase: UpdateAppointmentInRoomUseCase
    private lateinit var getDatesFromCacheByAppointmentUseCase: GetDatesFromCacheByAppointmentUseCase
    private lateinit var getAppointmentFromCacheByIdUseCase: GetAppointmentFromCacheByIdUseCase
    private lateinit var validateAppointmentInfosUseCase: ValidateAppointmentInfosUseCase
    private lateinit var logger: Logger
    private lateinit var useCase: UpdateAppointmentInCacheUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        addAppointmentToCacheUseCase = mockk(relaxed = true)
        updateAppointmentInRoomUseCase = mockk(relaxed = true)
        getDatesFromCacheByAppointmentUseCase = mockk(relaxed = true)
        getAppointmentFromCacheByIdUseCase = mockk(relaxed = true)
        validateAppointmentInfosUseCase = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0])}
        every { logger.error(any()) } answers { println(it.invocation.args[0])}
        every { logger.warn(any()) } answers { println(it.invocation.args[0])}

        useCase = UpdateAppointmentInCacheUseCase(
            repository,
            addAppointmentToCacheUseCase,
            updateAppointmentInRoomUseCase,
            getDatesFromCacheByAppointmentUseCase,
            getAppointmentFromCacheByIdUseCase,
            validateAppointmentInfosUseCase,
            logger
        )
    }

    @Test
    @DisplayName("Should return Resource Success when appointment is successfully updated")
    fun `should return Resource Success when appointment is successfully updated`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Updated Appointment",
            notes = "Updated Notes",
            color = 1,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusDays(1),
            startTime = LocalTime.now(),
            endTime = LocalTime.now().plusHours(1),
            hasBeenSynced = true
        )

        val oldAppointment = appointment.copy(startDate = LocalDate.now().minusDays(1), endDate = LocalDate.now())
        val oldDates = listOf(20230101, 20230102)

        coEvery { validateAppointmentInfosUseCase.invoke(appointment) } returns Resource.Success(appointment)
        coEvery { updateAppointmentInRoomUseCase.invoke(appointment) } returns Resource.Success(true)
        coEvery { getAppointmentFromCacheByIdUseCase.invoke(appointment.id) } returns Resource.Success(oldAppointment)
        coEvery { getDatesFromCacheByAppointmentUseCase.invoke(appointment.id) } returns Resource.Success(oldDates)
        coEvery { repository.deleteAppointmentFromByDateCache(any(), appointment.id) } returns Resource.Success(true)
        coEvery { repository.clearDateByAppointment(appointment.id) } returns Resource.Success(true)
        coEvery { repository.generateDateKey(any(), any(), any()) } returnsMany listOf(20230103, 20230104)
        coEvery { repository.addDateToByAppointmentCache(appointment.id, any()) } returns Resource.Success(true)
        coEvery { repository.updateAppointmentInCache(appointment) } returns Resource.Success(true)

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Success)

        coVerify { validateAppointmentInfosUseCase.invoke(appointment) }
        coVerify { updateAppointmentInRoomUseCase.invoke(appointment) }
        coVerify { getAppointmentFromCacheByIdUseCase.invoke(appointment.id) }
        coVerify { getDatesFromCacheByAppointmentUseCase.invoke(appointment.id) }
        coVerify { repository.deleteAppointmentFromByDateCache(20230101, appointment.id) }
        coVerify { repository.clearDateByAppointment(appointment.id) }
        coVerify { repository.addDateToByAppointmentCache(appointment.id, 20230103) }
        coVerify { repository.addDateToByAppointmentCache(appointment.id, 20230104) }
        coVerify { repository.updateAppointmentInCache(appointment) }
        verify { logger.info("Validating appointment for update.") }
        verify { logger.info("Appointment updated successfully.") }
    }

    @Test
    @DisplayName("Should return Resource Error when validation fails")
    fun `should return Resource Error when validation fails`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Invalid Appointment",
            notes = "Invalid Notes",
            color = 1,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now(),
            hasBeenSynced = true
        )

        coEvery { validateAppointmentInfosUseCase.invoke(appointment) } returns Resource.Error("Validation failed")

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Error)
        assertEquals("Validation failed",(result as Resource.Error).message)

        coVerify { validateAppointmentInfosUseCase.invoke(appointment) }
        verify { logger.info("Validating appointment for update.") }
    }

    @Test
    @DisplayName("Should return Resource Error when update in room fails")
    fun `should return Resource Error when update in room fails`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Appointment",
            notes = "Notes",
            color = 1,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now(),
            hasBeenSynced = true
        )

        coEvery { validateAppointmentInfosUseCase.invoke(appointment) } returns Resource.Success(appointment)
        coEvery { updateAppointmentInRoomUseCase.invoke(appointment) } returns Resource.Error("Failed to update in Room")

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Error)
        assertEquals("Failed to update in Room",(result as Resource.Error).message)

        coVerify { validateAppointmentInfosUseCase.invoke(appointment) }
        coVerify { updateAppointmentInRoomUseCase.invoke(appointment) }
        verify { logger.info("Validating appointment for update.") }
    }

    @Test
    @DisplayName("Should call AddAppointmentUseCase and return Resource Success when old appointment does not exist in cache anymore")
    fun `should call AddAppointmentUseCase and return Resource Error when old appointment does not exist in cache anymore`() = runTest {
        val appointment = Appointment(
            id = 1,
            title = "Appointment",
            notes = "Notes",
            color = 1,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            startTime = LocalTime.now(),
            endTime = LocalTime.now(),
            hasBeenSynced = true
        )

        coEvery { validateAppointmentInfosUseCase.invoke(appointment) } returns Resource.Success(appointment)
        coEvery { updateAppointmentInRoomUseCase.invoke(appointment) } returns Resource.Success(true)
        coEvery { getAppointmentFromCacheByIdUseCase.invoke(appointment.id) } returns Resource.Error("Not found")
        coEvery { addAppointmentToCacheUseCase.invoke(appointment) } returns Resource.Success(true)

        val result = useCase.invoke(appointment)

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        coVerify { validateAppointmentInfosUseCase.invoke(appointment) }
        coVerify { updateAppointmentInRoomUseCase.invoke(appointment) }
        coVerify { getAppointmentFromCacheByIdUseCase.invoke(appointment.id) }
        coVerify { addAppointmentToCacheUseCase.invoke(appointment) }
        verify { logger.info("Validating appointment for update.") }
        verify { logger.warn("Old appointment doest not exist in cache anymore, changing to create operation instead an update.") }
    }
}