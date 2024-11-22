package com.example.schedule.feature_schedule.data.data_source.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AppointmentApi {

    @GET("Appointments")
    suspend fun getAppointments(): List<AppointmentDto>

    @POST("Appointments")
    suspend fun updateAppointments(@Body appointments: List<AppointmentDto>)

    companion object {
        const val BASE_URL = "https://66f32e6971c84d8058780e51.mockapi.io/retrofit/api/v1/"
    }
}