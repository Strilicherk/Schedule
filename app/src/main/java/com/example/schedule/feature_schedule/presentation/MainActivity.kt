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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CalendarTheme {
                CreateOrUpdateAppointmentScreen()
            }
        }
    }
}

