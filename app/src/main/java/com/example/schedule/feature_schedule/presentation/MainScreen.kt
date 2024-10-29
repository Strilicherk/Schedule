package com.example.schedule.feature_schedule.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.unit.dp
import com.example.schedule.feature_schedule.presentation.schedule_menu.components.AppointmentListComponent
import com.example.schedule.feature_schedule.presentation.schedule_menu.components.DailyAppointmentListComponent
import com.example.schedule.feature_schedule.presentation.schedule_menu.components.MonthScheduleGridComponent
import com.example.schedule.feature_schedule.presentation.schedule_menu.components.ScheduleHeaderComponent

@Composable
fun MainScreen() {
    Scaffold(
        topBar = {
            Row (
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(0.dp, 55.dp, 0.dp, 0.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.05f)

            ){
                ScheduleHeaderComponent()
            }
        },
        floatingActionButton = {

        },
        content = { innerPadding ->
            Column (
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                MonthScheduleGridComponent()
                DailyAppointmentListComponent()
            }

        }
    )
}