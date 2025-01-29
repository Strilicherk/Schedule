package com.example.schedule.feature_schedule.presentation.schedule_menu.components

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schedule.feature_schedule.domain.model.Day
import com.example.schedule.feature_schedule.presentation.schedule_menu.ScheduleEvents
import com.example.schedule.feature_schedule.presentation.schedule_menu.ScheduleViewModel
import com.example.schedule.ui.theme.CustomRed
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.Year
import java.time.YearMonth
import kotlin.math.log

val TAG = "MonthLogDScreen"

@Composable
fun ScheduleComponent(
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val lazyListState = rememberLazyListState(state.currentIndex)
    val coroutineScope = rememberCoroutineScope()
    var job by remember { mutableStateOf<Job?>(null) }

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
                    detectDragGestures { change, dragAmount ->
                        changeVisibleMonth(
                            viewModel = viewModel,
                            dragAmount = dragAmount,
                            lazyListState = lazyListState,
                            coroutineScope = coroutineScope,
                            jobState = { newJob ->
                                val oldJob = job
                                job = newJob
                                oldJob
                            }
                        )
                    }
                }) {
            items(viewModel.monthsData.flatMap { (year, monthsMap) ->
                monthsMap.entries.map { year to it }
            }) { (year, monthEntry) ->
                val (month, monthDays) = monthEntry

                MonthStructure(
                    days = monthDays,
                    lazyListState = lazyListState,
                    coroutineScope = coroutineScope,
                    onDayClick = { date ->
                        viewModel.onEvent(ScheduleEvents.SelectDay(date))
                    }
                )
            }
        }
    }
}

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
                    style = typography.displaySmall
                )
            }
        }
    }
}

fun changeVisibleMonth(
    viewModel: ScheduleViewModel,
    dragAmount: Offset? = null,
    day: Day? = null,
    lazyListState: LazyListState,
    coroutineScope: CoroutineScope,
    jobState: (Job?) -> Job?,
) {
    val state = viewModel.state.value
    var currentIndex = state.currentIndex
    val currentJob = jobState(null)
    currentJob?.cancel()

    val newJob = coroutineScope.launch {
        delay(100)
        val totalItems = lazyListState.layoutInfo.totalItemsCount

        dragAmount?.let { drag ->
            val (dragX, _) = drag
            Log.d(TAG, "Detectado arrasto. dragX: $dragX")

            if (dragX > 0 && currentIndex > 0) {
                currentIndex--
                Log.d(TAG, "Movendo para o mês anterior. currentIndex: $currentIndex")
            }
            if (dragX < 0 && currentIndex < totalItems - 1) {
                currentIndex++
                Log.d(TAG, "Movendo para o próximo mês. currentIndex: $currentIndex")
            }
        }

        day?.let {
            val year = state.currentYear
            val month = state.currentMonth

            if (it.date.isBefore(LocalDate.of(year, month, 1))) {
                if (currentIndex > 0) {
                    currentIndex--
                    Log.d(TAG, "Movendo para o mês anterior após clique. currentIndex: $currentIndex")
                }
            } else if (it.date.isAfter(LocalDate.of(year, month, YearMonth.of(year, month).lengthOfMonth()))) {
                if (currentIndex < totalItems - 1) {
                    currentIndex++
                    Log.d(TAG, "Movendo para o próximo mês após clique. currentIndex: $currentIndex")
                }
            }
        }

        val year = Year.now().value + (currentIndex / 12) - 1
        val month = Month.entries[currentIndex % 12]

        viewModel.onEvent(ScheduleEvents.UpdateViewingDate(year, month, currentIndex))
        lazyListState.animateScrollToItem(currentIndex)
    }
    jobState(newJob)
}


@Composable
fun MonthStructure(
    viewModel: ScheduleViewModel = hiltViewModel(),
    days: List<Day>,
    lazyListState: LazyListState,
    coroutineScope: CoroutineScope,
    onDayClick: (LocalDate) -> Unit
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val rows = days.chunked(7)
    var job by remember { mutableStateOf<Job?>(null) }

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
                                        changeVisibleMonth(
                                            viewModel = viewModel,
                                            day = day,
                                            lazyListState = lazyListState,
                                            coroutineScope = coroutineScope,
                                            jobState = { newJob ->
                                                val oldJob = job
                                                job = newJob
                                                oldJob
                                            }
                                        )
                                    }
                                    selectedDate = day.date
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
                                fontSize = typography.displaySmall.fontSize
                            )
                        }
                    }
                }
            }
        }
    }
}

fun generateDateKey(day: Int, month: Int, year: Int): Int {
    return day * 1000000 + month * 10000 + year
}

