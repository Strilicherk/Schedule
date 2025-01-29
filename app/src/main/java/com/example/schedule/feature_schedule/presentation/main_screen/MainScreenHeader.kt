package com.example.schedule.feature_schedule.presentation.main_screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schedule.feature_schedule.presentation.schedule_menu.ScheduleViewModel

@Composable
fun MainScreenHeader(
    scheduleViewModel: ScheduleViewModel = hiltViewModel()
) {
    val scheduleState = scheduleViewModel.state.collectAsState()
    val scheduleStateValue = scheduleState.value
    val monthName = scheduleStateValue.currentMonth.name.substring(0, 3).lowercase()
        .replaceFirstChar { it.uppercase() } + " " + scheduleStateValue.currentYear

    Row(
        modifier = Modifier
            .padding(20.dp,0.dp)
            .fillMaxWidth()
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = monthName,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
            color = White
        )
    }
}
