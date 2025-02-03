package com.example.schedule.feature_schedule.presentation.components.main_screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schedule.feature_schedule.domain.model.Day
import com.example.schedule.feature_schedule.presentation.events.MainScreenEvents
import com.example.schedule.feature_schedule.presentation.events.CalendarEvents
import com.example.schedule.feature_schedule.presentation.view_model.CalendarViewModel
import com.example.schedule.feature_schedule.presentation.view_model.MainScreenViewModel
import com.example.schedule.ui.theme.CustomRed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate

val TAG = "MonthLogDScreen"

@Composable
fun ScheduleHeader() {
    Row(
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 15.dp)
            .fillMaxWidth()
            .fillMaxHeight(0.1f)
    ) {
        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
        daysOfWeek.forEachIndexed { index, day ->
            Box(
                modifier = Modifier
                    .width(54.dp)
                    .padding(start = 2.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = day,
                    color = if (index == 0) CustomRed else White,
                    style = typography.displaySmall,
                    modifier = Modifier.padding(5.dp, 0.dp, 0.dp, 0.dp)
                )
            }
        }
    }
}

@Composable
fun CalendarComponent(
    viewModel: CalendarViewModel = hiltViewModel(),
    screen: String
) {
    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    var scrollJob by remember { mutableStateOf<Job?>(null) }

    val currentIndex: Int = if (screen == "Main") {
        state.currentMainScreenMonthIndex
    } else {
        state.currentAppointmentScreenMonthIndex
    }

    LaunchedEffect(state) {
        val updatedIndex = if (screen == "Main") {
            state.currentMainScreenMonthIndex
        } else {
            state.currentAppointmentScreenMonthIndex
        }
        Log.d("CalendarDebug", "Animating to new index: $updatedIndex")
        lazyListState.animateScrollToItem(updatedIndex)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f)
            .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(30.dp))
    ) {
        ScheduleHeader()
        LazyRow(
            state = lazyListState,
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .align(Alignment.BottomStart)
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->

                        viewModel.onEvent(
                            CalendarEvents.ChangeVisibleMonth(
                                dragAmount = dragAmount,
                                day = null,
                                totalItems = lazyListState.layoutInfo.totalItemsCount,
                                screen = screen
                            )
                        )
                    }
                }) {
            state.monthsData.forEach { (_, monthsMap) ->
                monthsMap.forEach { (_, monthDays) ->
                    item {
                        Log.d(
                            "CalendarDebug",
                            "currentIndex pre chamada monthstructure = $currentIndex"
                        )
                        MonthStructure(
                            days = monthDays,
                            screen = screen,
                            currentIndex = currentIndex,
                            selectedDate = state.selectedDate,
                            lazyListState = lazyListState,
                            onDayClick = { date ->
                                viewModel.onEvent(CalendarEvents.SelectDay(date))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MonthStructure(
    viewModel: CalendarViewModel = hiltViewModel(),
    screen: String,
    currentIndex: Int,
    days: List<Day>,
    selectedDate: LocalDate,
    lazyListState: LazyListState,
    onDayClick: (LocalDate) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val rows = days.chunked(7)
    Log.d("CalendarDebug", "currentIndex monthSctructure = $currentIndex")

    Box(
        modifier = Modifier
            .width(screenWidth.dp)
            .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .fillMaxSize()
        ) {
            rows.forEach { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    week.forEach { day ->
                        val isSelected = selectedDate == day.date
                        val textColor = when {
                            day.date.dayOfWeek == DayOfWeek.SUNDAY && day.isCurrentMonth -> Color.Red
                            day.date.dayOfWeek == DayOfWeek.SUNDAY && !day.isCurrentMonth -> Color.Red.copy(
                                alpha = 0.5f
                            )

                            day.isCurrentMonth -> White
                            else -> Gray
                        }

                        Box(
                            modifier = Modifier
                                .width(54.dp)
                                .height(48.dp)
                                .clickable(enabled = true) {
                                    if (!day.isCurrentMonth) {
                                        Log.d(TAG, "")
                                        coroutineScope.launch {
                                            Log.d(
                                                "CalendarDebug",
                                                "currentIndex pre withContext= $currentIndex"
                                            )
                                            withContext(Dispatchers.Main) {
                                                viewModel.onEvent(
                                                    CalendarEvents.ChangeVisibleMonth(
                                                        dragAmount = null,
                                                        day = day,
                                                        totalItems = lazyListState.layoutInfo.totalItemsCount,
                                                        screen = screen
                                                    )
                                                )
                                            }
                                        }
                                    }
                                    onDayClick(day.date)
                                }
                                .then(
                                    if (isSelected) {
                                        Modifier.border(1.dp, Color.Cyan, RoundedCornerShape(10.dp))
                                    } else Modifier
                                ),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Text(
                                text = day.date.dayOfMonth.toString(),
                                color = textColor,
                                fontSize = typography.displaySmall.fontSize,
                                modifier = Modifier.padding(5.dp, 0.dp, 0.dp, 0.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

