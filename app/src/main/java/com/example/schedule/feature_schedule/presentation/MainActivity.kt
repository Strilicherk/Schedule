package com.example.schedule.feature_schedule.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.schedule.feature_schedule.presentation.view_add_edit_appointment.components.CreateOrUpdateAppointmentScreen
import com.example.schedule.ui.theme.CalendarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity(
//    private val api: ScheduleApi
) : ComponentActivity() {
//    val TAG = "Testando API na Main"
//
//    init {
//        val apiReturn = api.getAppointments()
//        Log.d(TAG, "$apiReturn")
//    }

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

