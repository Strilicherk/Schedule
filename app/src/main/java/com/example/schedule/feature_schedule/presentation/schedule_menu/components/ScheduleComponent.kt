package com.example.schedule.feature_schedule.presentation.schedule_menu.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.domain.model.Day
import com.example.schedule.feature_schedule.domain.repository.AppointmentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

val TAG = "MonthLogDScreen"
var currentIndex = 0

@Composable
fun ScheduleComponent(repository: AppointmentRepository) {
    // Inicializações e estado
    val today = remember { LocalDate.now() }
    var currentYear by remember { mutableStateOf(today.year) }
    var currentMonthIndex by remember { mutableStateOf(today.monthValue - 1) }

    val months = (1..12).toList()
    val coroutineScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()

    // Simulação de caches
    val appointmentByDateCache = remember {
        mutableStateMapOf(
            9012025 to mutableListOf(1, 2, 3),
            10012025 to mutableListOf(4)
        )
    }
    val appointmentCache = remember {
        mutableStateMapOf(
            1 to Appointment(
                id = 1,
                title = "Reunião",
                notes = "Discussão sobre projetos",
                color = 0xFF0000, // Example color in hexadecimal
                startDate = LocalDate.of(2025, 1, 21),
                endDate = LocalDate.of(2025, 1, 21),
                startTime = LocalTime.of(10,0),
                endTime = LocalTime.of(11, 0)
            ),
            2 to Appointment(
                id = 2,
                title = "Consulta médica",
                notes = "Consulta geral",
                color = 0x00FF00,
                startDate = LocalDate.of(2025, 1, 22),
                endDate = LocalDate.of(2025, 1, 22),
                startTime = LocalTime.of(14, 0),
                endTime = LocalTime.of(15, 0)
            ),
            3 to Appointment(
                id = 3,
                title = "Jantar",
                notes = "Jantar com amigos",
                color = 0x0000FF,
                startDate = LocalDate.of(2025, 1, 23),
                endDate = LocalDate.of(2025, 1, 23),
                startTime = LocalTime.of(19, 30),
                endTime = LocalTime.of(22, 0)
            ),
            4 to Appointment(
                id = 4,
                title = "Aniversário",
                notes = "Festa de aniversário",
                color = 0xFFFF00,
                startDate = LocalDate.of(2025, 1, 24),
                endDate = LocalDate.of(2025, 1, 24),
                startTime = LocalTime.of(18, 0),
                endTime = LocalTime.of(22, 0)
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f)
            .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(30.dp))
    ) {
        LazyRow(
            state = lazyListState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(months) { month ->
                MonthStructure(
                    year = currentYear,
                    month = month,
                    lazyListState = lazyListState,
                    coroutineScope = coroutineScope,
                    repository = repository // Passa o repositório
                ) { dateKey, date ->
                    // Recuperar compromissos do cache
                    val appointments = appointmentByDateCache[dateKey]
                    Log.d(
                        TAG,
                        "Data Clicada: $dateKey ($date) - Compromissos: ${
                            appointments?.joinToString(", ") { id ->
                                appointmentCache[id]?.title ?: "Desconhecido"
                            }
                        }"
                    )
                }
            }
        }
    }
}


@Composable
fun MonthStructure(
    year: Int,
    month: Int,
    lazyListState: LazyListState,
    coroutineScope: CoroutineScope,
    repository: AppointmentRepository, // Repositório injetado
    onDayClick: (Int, LocalDate) -> Unit
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val days = remember(year, month) { getMonthDays(year, month) }
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    week.forEach { day ->
                        val isSelected = selectedDate == day.date
                        Box(
                            modifier = Modifier
                                .width(54.dp)
                                .height(48.dp)
                                .clickable(enabled = true) {
                                    selectedDate = day.date
                                    coroutineScope.launch {
                                        val dateKey = repository.generateDateKey(
                                            day.date.dayOfMonth,
                                            day.date.monthValue,
                                            day.date.year
                                        )
                                        onDayClick(dateKey, day.date)
                                    }
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
                                color = if (day.isCurrentMonth) White else Gray,
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
    val yearMonth = YearMonth.of(year, month)
    val firstDayOfMonth = LocalDate.of(year, month, 1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()

    // Dias do mês anterior
    val daysBefore = (firstDayOfMonth.dayOfWeek.value % 7).let { offset ->
        (1..offset).map { i ->
            Day(
                date = firstDayOfMonth.minusDays(offset.toLong() - i + 1),
                isCurrentMonth = false
            )
        }
    }

    // Dias do mês atual
    val currentMonthDays = (1..yearMonth.lengthOfMonth()).map { day ->
        Day(
            date = LocalDate.of(year, month, day),
            isCurrentMonth = true
        )
    }

    // Dias do próximo mês para completar 42 células
    val totalDays = daysBefore.size + currentMonthDays.size
    val remainingDays = 42 - totalDays
    val daysAfter = (1..remainingDays).map { i ->
        Day(
            date = lastDayOfMonth.plusDays(i.toLong()),
            isCurrentMonth = false
        )
    }

    return daysBefore + currentMonthDays + daysAfter
}