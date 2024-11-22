package com.example.schedule.feature_schedule.presentation

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.schedule.feature_schedule.data.data_source.local.AppointmentDatabase
import com.example.schedule.feature_schedule.data.data_source.local.AppointmentEntity
import com.example.schedule.feature_schedule.presentation.view_add_edit_appointment.components.CreateOrUpdateAppointmentScreen
import com.example.schedule.ui.theme.CalendarTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var db: AppointmentDatabase
    private val dao by lazy { db.dao }

    val TAG = "Teste Database"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CalendarTheme {
                CreateOrUpdateAppointmentScreen()
            }
        }

        val insertAppointmentsList: List<AppointmentEntity> = listOf(
            AppointmentEntity(
                title = "Teste 1",
                notes = "English lessons with Edimar",
                color = 1,
                startDate = LocalDate.of(2020, 5, 1).toEpochDay(),
                endDate = LocalDate.of(2020, 10, 1).toEpochDay(),
                startTime = "08:00",
                endTime = "10:00",
                lastModified = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                isSynced = false
            ),
            AppointmentEntity(
                title = "Teste 2",
                notes = "English lessons with Edimar",
                color = 1,
                startDate = LocalDate.of(2024, 5, 1).toEpochDay(),
                endDate = LocalDate.of(2024, 10, 1).toEpochDay(),
                startTime = "08:00",
                endTime = "10:00",
                lastModified = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                isSynced = true
            )
        )

        GlobalScope.launch {
//            dao.selectAppointments() --
//            dao.selectUnsyncedAppointments() --
//            dao.selectAppointmentsOfTheYear() --
//            dao.upsertAppointment() --
//            dao.deleteAppointment()
            insertAppointmentsList.forEach { dao.upsertAppointment(it) }

            val allAppointments = dao.selectAppointments()
            Log.d(TAG, "Todos: $allAppointments")

            delay(1000)

            val unsyncedAppointments = dao.selectUnsyncedAppointments()
            Log.d(TAG, "Não Sync: $unsyncedAppointments")

            val startOfTheYear = LocalDate.of(2024, 1, 1).toEpochDay()
            val endOfTheYear = LocalDate.of(2024, 12, 31).toEpochDay()

            var appointmentsOf2024 = dao.selectAppointmentsOfTheYear(startOfTheYear, endOfTheYear)
            Log.d(TAG, "2024: $appointmentsOf2024")

            delay(30000)
            Log.d(TAG, "Deletando: ${appointmentsOf2024[1]}")
            delay(3000)
            dao.deleteAppointment(appointmentsOf2024[1].id)
            Log.d(TAG, "Deletado")
            delay(3000)
            appointmentsOf2024 = dao.selectAppointmentsOfTheYear(startOfTheYear, endOfTheYear)
            Log.d(TAG, "2024: $appointmentsOf2024")


        }

    }
}

