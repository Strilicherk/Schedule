package com.example.schedule.feature_schedule.presentation.schedule_menu.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate

@Composable
fun ScheduleHeaderComponent() {
    val month = LocalDate.now().month.toString().substring(0,3).lowercase().replaceFirstChar{ it.uppercase() } + " " + LocalDate.now().year
    Row (
        modifier = Modifier
            .padding(15.dp, 0.dp)

    ) {
        Text(
            text = month,
            fontSize = 20.sp
        )
    }
}