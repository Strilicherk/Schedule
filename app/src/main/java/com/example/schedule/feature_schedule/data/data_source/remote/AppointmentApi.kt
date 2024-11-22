package com.example.schedule.feature_schedule.data.data_source.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface AppointmentApi {

    @GET("Appointments")
    suspend fun getAppointments(): List<AppointmentDto>

    @POST("Appointments")
    suspend fun postAppointments(@Body appointments: List<AppointmentDto>): Response<Any>

    @DELETE("Appointments/{id}")
    suspend fun deleteAppointment(@Path("id") id: Int): Response<Any>

    companion object {
        const val BASE_URL = "https://66f32e6971c84d8058780e51.mockapi.io/retrofit/api/v1/"
    }
}