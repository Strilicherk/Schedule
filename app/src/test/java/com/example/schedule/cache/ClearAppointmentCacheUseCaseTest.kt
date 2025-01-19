package com.example.schedule.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.ClearAppointmentCacheUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import java.io.IOException

class ClearAppointmentCacheUseCaseTest {

    private lateinit var repository: AppointmentRepository
    private lateinit var logger: Logger
    private lateinit var useCase: ClearAppointmentCacheUseCase

    @BeforeEach
    fun setup() {
        repository = mockk(relaxed = true)
        logger = mockk()
        every { logger.info(any()) } answers { println(it.invocation.args[0]) }
        every { logger.warn(any()) } answers { println(it.invocation.args[0]) }
        every { logger.error(any()) } answers { println(it.invocation.args[0]) }
        useCase = ClearAppointmentCacheUseCase(repository, logger)
    }

    @Test
    @DisplayName("Should log the action and clear the cache successfully")
    fun `should log the action and clear the cache successfully`() = runTest {
        coEvery { repository.clearCache() } returns Resource.Success(true)
        val result = useCase()

        assertTrue(result is Resource.Success)
        assertEquals(true, (result as Resource.Success).data)

        verify { logger.info("Clearing appointment cache.") }
        coVerify(exactly = 1) { repository.clearCache() }
    }

    @Test
    @DisplayName("Should log the action and return an error if clearing cache fails")
    fun `should log the action and return an error if clearing cache fails`() = runTest {
        coEvery { repository.clearCache() } returns Resource.Error("Failed to clear cache")
        val result = useCase()

        assertTrue(result is Resource.Error)
        assertEquals("Failed to clear cache", (result as Resource.Error).message)

        verify { logger.info("Clearing appointment cache.") }
        coVerify(exactly = 1) { repository.clearCache() }
    }
}