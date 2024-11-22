package com.example.schedule.feature_schedule.presentation.schedule_menu.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.schedule.ui.theme.CustomCyan
import com.example.schedule.ui.theme.CustomRed
import java.time.LocalDate

val TAG = "MonthLogDScreen"


@Composable
fun ScheduleComponent() {
    val months = listOf(
        "Janeiro",
        "Fevereiro",
        "Março",
        "Abril",
        "Maio",
        "Junho",
        "Julho",
        "Agosto",
        "Setembro",
        "Outubro",
        "Novembro",
        "Dezembro"
    )



    val lazyListState = rememberLazyListState()
    val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f),
        state = lazyListState,
        flingBehavior = snapBehavior
    ) {
        items(items = months, itemContent = { month ->
            Column {
                ScheduleHeader(month)
                MonthStructure()
            }
        })
    }
}

@Composable
fun ScheduleHeader(month: Any) {
    val monthName = month.toString().substring(0, 3).lowercase()
        .replaceFirstChar { it.uppercase() } + " " + LocalDate.now().year
    Row(
        modifier = Modifier
            .padding(55.dp, 0.dp, 0.dp, 15.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = monthName,
            fontSize = MaterialTheme.typography.titleLarge.fontSize,
            fontWeight = MaterialTheme.typography.titleLarge.fontWeight
        )
    }
}

@Composable
fun MonthStructure() {
    var selectedDate by remember { mutableStateOf("") }
    var selectedBorder by remember { mutableStateOf("") }
    var borderValue by remember { mutableStateOf(false) }

    val typography = MaterialTheme.typography
    val screenWidth = LocalConfiguration.current.screenWidthDp

    val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
    val daysNumber = (1..30).toList()
    val days = daysOfWeek + daysNumber
    val rows = days.chunked(7)
    var alterna = 0

    Box(
        modifier = Modifier
            .width(screenWidth.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .fillMaxSize()
        ) {
            rows.forEachIndexed { weekIndex, week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    week.forEachIndexed { dayIndex, day ->
                        var heightValue = 48.dp
                        var alignmentValue = Alignment.TopStart
                        var colorValue = White
                        var fontSizeValue = typography.displayMedium.fontSize
                        if (weekIndex == 0) {
                            heightValue = 23.dp
                            alignmentValue = Alignment.BottomStart
                            fontSizeValue = typography.displaySmall.fontSize
                        }

                        if (dayIndex == 0) colorValue = CustomRed

                        alterna++
                        Box(
                            modifier = Modifier
                                .width(54.dp)
                                .height(heightValue)
                                .clickable {
                                    selectedBorder = day.toString()
                                }
                                .then(
                                    if (selectedBorder == day.toString()) {
                                        Modifier.border(1.dp, CustomCyan, RoundedCornerShape(10.dp))
                                    } else {
                                        Modifier
                                    }
                                )
//                                .background(if (alterna % 2 == 0) Black else White)
                            , contentAlignment = alignmentValue
                        ) {
                            Text(
                                text = day.toString(),
                                color = colorValue,
                                fontSize = fontSizeValue,
                                modifier = Modifier
                                    .padding(3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}