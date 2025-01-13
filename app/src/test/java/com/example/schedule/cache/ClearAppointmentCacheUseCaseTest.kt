package com.example.schedule.cache

import com.example.schedule.feature_schedule.common.Resource
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.domain.use_case.appointment.cache.ClearAppointmentCacheUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.io.IOException

class ClearAppointmentCacheUseCaseTest {
    private lateinit var repository: AppointmentRepository
    private lateinit var useCase: ClearAppointmentCacheUseCase

    @BeforeEach
    fun Setup() {
        repository = mockk()
        useCase = ClearAppointmentCacheUseCase(repository)
    }

    @Test
    @DisplayName("Should return Resource Error when cache is unable to be cleaned")
    fun `should return Resource Error when cache is unable to be cleaned` () = runTest {
        coEvery { repository.clearCache() } returns Resource.Error("Unable to clear cache")

        val result = useCase.invoke()
        assertTrue(result is Resource.Error)
        assertEquals("Unable to clear cache", (result as Resource.Error).message)
    }

    @Test
    @DisplayName("Should return Resource Error when clearCache throws IOException")
    fun `should return Resource Error when clearCache throws IOException` () = runTest {
        coEvery { repository.clearCache() } throws IOException("Network Error")

        val result = useCase.invoke()
        assertTrue(result is Resource.Error)
        assertEquals("IO Exception: java.io.IOException: Network Error", (result as Resource.Error).message)
    }

    @Test
    @DisplayName("Should return Resource Error when clearCache throws Exception")
    fun `should return Resource Error when clearCache throws Exception` () = runTest {
        coEvery { repository.clearCache() } throws Exception("Unknown Error")

        val result = useCase.invoke()
        assertTrue(result is Resource.Error)
        assertEquals("Exception: java.lang.Exception: Unknown Error", (result as Resource.Error).message)
    }
}