package com.example.schedule.feature_schedule.presentation.schedule_menu.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollScope
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.model.Day
import com.example.schedule.feature_schedule.domain.model.MonthData
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import com.example.schedule.feature_schedule.presentation.schedule_menu.ScheduleEvents
import com.example.schedule.feature_schedule.presentation.schedule_menu.ScheduleViewModel
import com.example.schedule.ui.theme.CustomRed
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.Month
import java.time.YearMonth

val TAG = "MonthLogDScreen"
var currentIndex = 0

@Composable
fun ScheduleComponent(
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    Log.d(TAG, "ScheduleComponent")
    val state by viewModel.state
    val today = remember { LocalDate.now() }
    var currentYear by remember { mutableStateOf(today.year) }
    var currentMonthIndex by remember { mutableStateOf(today.monthValue - 1) }
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    var job by remember { mutableStateOf<Job?>(null) }

    val monthsData = remember {
        listOf(
            2024 to Month.entries.associateWith { month -> getMonthDays(2024, month.value) },
            2025 to Month.entries.associateWith { month -> getMonthDays(2025, month.value) },
            2026 to Month.entries.associateWith { month -> getMonthDays(2026, month.value) }
        )
    }
    Log.d(TAG, "$monthsData")
    Log.d(TAG, "${monthsData.size}")

    LaunchedEffect(lazyListState.firstVisibleItemIndex) {
        val visibleIndex = lazyListState.firstVisibleItemIndex
        if (visibleIndex != currentMonthIndex) {
            val delta = visibleIndex - currentMonthIndex
            currentMonthIndex = visibleIndex

            // Atualiza o ano dinamicamente ao mudar o mês
            if (delta > 0 && visibleIndex % 12 == 0) {
                currentYear++
            } else if (delta < 0 && visibleIndex % 12 == 11) {
                currentYear--
            }
        }
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
                    detectDragGestures { change, dragAmount ->
                        handleDragGesture(
                            viewModel = viewModel,
                            change,
                            dragAmount,
                            lazyListState,
                            coroutineScope,
                            jobState = { newJob ->
                                val oldJob = job
                                job = newJob
                                oldJob
                            },
                            onMonthChange = { year, month ->
                                viewModel.onEvent(ScheduleEvents.UpdateViewingDate(year, month))
                            }
                        )
                    }
                }) {
            items(monthsData.flatMap { (year, monthsMap) -> monthsMap.entries.map { year to it } }) { (year, monthEntry) ->
                val (month, monthDays) = monthEntry

                MonthStructure(
                    year = year, // Passa o ano correto
                    month = month, // Obtém o índice do mês
                    days = monthDays, // Passa os dias do mês
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
    Log.d(TAG, "Header")
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

fun handleDragGesture(
    viewModel: ScheduleViewModel,
    change: PointerInputChange,
    dragAmount: Offset,
    lazyListState: LazyListState,
    coroutineScope: CoroutineScope,
    jobState: (Job?) -> Job?,
    onMonthChange: (Int, Month) -> Unit
) {
    change.consume()
    val (dragX, _) = dragAmount

    val currentJob = jobState(null)
    currentJob?.cancel()

    val newJob = coroutineScope.launch {
        delay(100)
        // Calcula o novo mês e ano
        val year = 2024 + (currentIndex / 12)
        val monthIndex = currentIndex % 12
        val month = Month.entries[monthIndex]

        // Atualiza o estado do mês e ano visível
        onMonthChange(year, month)

        val totalItems = lazyListState.layoutInfo.totalItemsCount
        if (dragX > 0 && currentIndex > 0) {
            currentIndex--
        } else if (dragX < 0 && currentIndex < totalItems - 1) {
            currentIndex++
        }
        lazyListState.animateScrollToItem(currentIndex)
    }
    jobState(newJob)


}


@Composable
fun MonthStructure(
    year: Int,
    month: Month,
    days: List<Day>, // Recebe a lista de dias já calculada
    lazyListState: LazyListState,
    coroutineScope: CoroutineScope,
    onDayClick: (LocalDate) -> Unit
) {
    Log.d(TAG, "MonthStructure")
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val rows = days.chunked(7)

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

                            day.isCurrentMonth -> Color.White
                            else -> Color.Gray // Para os dias fora do mês
                        }

                        Box(
                            modifier = Modifier
                                .width(54.dp)
                                .height(48.dp)
                                .clickable(enabled = true) {
                                    if (!day.isCurrentMonth) {
                                        coroutineScope.launch {
                                            if (day.date.isBefore(LocalDate.of(year, month, 1))) {
                                                // Navegar para o mês anterior
                                                if (currentIndex > 0) {
                                                    currentIndex--
                                                    lazyListState.animateScrollToItem(currentIndex)
                                                }
                                            } else if (day.date.isAfter(
                                                    LocalDate.of(
                                                        year,
                                                        month,
                                                        YearMonth.of(year, month).lengthOfMonth()
                                                    )
                                                )
                                            ) {
                                                // Navegar para o próximo mês
                                                if (currentIndex < lazyListState.layoutInfo.totalItemsCount - 1) {
                                                    currentIndex++
                                                    lazyListState.animateScrollToItem(currentIndex)
                                                }
                                            }
                                        }
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

fun getMonthDays(year: Int, month: Int): List<Day> {
    Log.d(TAG, "getMonthDays")
    val yearMonth = YearMonth.of(year, month)
    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()

    // Dias do mês anterior
    val daysBefore = (firstDayOfMonth.dayOfWeek.value % 7).let { offset ->
        (1..offset).map { i ->
            Day(
                dayOfWeek = firstDayOfMonth.minusDays(offset.toLong() - i + 1).dayOfWeek,
                date = firstDayOfMonth.minusDays(offset.toLong() - i + 1),
                isCurrentMonth = false
            )
        }
    }
    // Dias do mês atual
    val currentMonthDays = (1..yearMonth.lengthOfMonth()).map { day ->
        Day(
            dayOfWeek = LocalDate.of(year, month, day).dayOfWeek,
            date = LocalDate.of(year, month, day),
            isCurrentMonth = true
        )
    }

    // Dias do próximo mês para completar 42 células
    val totalDays = daysBefore.size + currentMonthDays.size
    val remainingDays = 42 - totalDays
    val daysAfter = (1..remainingDays).map { i ->
        Day(
            dayOfWeek = lastDayOfMonth.plusDays(i.toLong()).dayOfWeek,
            date = lastDayOfMonth.plusDays(i.toLong()),
            isCurrentMonth = false
        )
    }

    return daysBefore + currentMonthDays + daysAfter
}

fun generateDateKey(day: Int, month: Int, year: Int): Int {
    return day * 1000000 + month * 10000 + year
}

