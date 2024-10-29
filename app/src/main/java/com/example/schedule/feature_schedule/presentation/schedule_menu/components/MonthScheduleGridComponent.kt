package com.example.schedule.feature_schedule.presentation.schedule_menu.components

import android.util.Log
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schedule.ui.theme.CustomCyan
import com.example.schedule.ui.theme.CustomRed
import com.example.schedule.ui.theme.Purple40

val TAG = "MonthLogDScreen"


@Composable
fun MonthScheduleGridComponent() {
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
            .fillMaxHeight(0.4f)
            .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(20.dp)),
        state = lazyListState,
        flingBehavior = snapBehavior
    ) {
        items(items = months, itemContent = { month ->
            GridItem()
        })
    }
}

@Composable
fun GridItem(
) {
    var selectedDate by remember { mutableStateOf<String>("") }
    var selectedBorder by remember { mutableStateOf<String>("") }

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
//            .background(if (alterna % 2 == 0) Red else Green, RoundedCornerShape(20.dp))
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp)
//                .background(Blue)
                .fillMaxSize()
        ) {
            rows.forEachIndexed { weekIndex, week ->
                Row(
                    modifier = Modifier
//                        .background(if (alterna % 2 == 0) Red else Green)
                        .fillMaxWidth()
                ) {
                    week.forEachIndexed { dayIndex, day ->
                        var heightValue = 52.dp
                        var alignmentValue = Alignment.TopStart
                        var colorValue = White
                        var fontSizeValue = typography.displayMedium.fontSize
                        if (weekIndex == 0) {
                            heightValue = 30.dp
                            alignmentValue = Alignment.BottomStart
                            fontSizeValue = typography.displaySmall.fontSize
                        }

                        if (dayIndex == 0) colorValue = CustomRed


                        alterna++
                        Box(
                            modifier = Modifier
                                .width(54.dp)
                                .height(heightValue)
                                .then(
                                    if (weekIndex != 0 && selectedBorder == day.toString()) {
                                        Modifier.border(1.dp, CustomCyan, RoundedCornerShape(10.dp))
                                    } else {
                                        Modifier // sem borda adicional
                                    }
                                )
//                                .background(if (alterna % 2 == 0) Black else White)
                            ,contentAlignment = alignmentValue
                        ) {
                            Text(
                                text = day.toString(),
                                color = colorValue,
                                fontSize = fontSizeValue,
                                modifier = Modifier
                                    .padding(3.dp)
                                    .fillMaxSize()
                                    .clickable {
                                        selectedBorder = day.toString()
                                        if (selectedDate == day.toString()) {

                                        }
                                    }
                            )
                        }
                    }
                }
            }
        }
//

        // Divida os dias em linhas de 7 itens
//    val rows = daysArray.chunked(7)

//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
////            .background(Green)
//    ) {
//        rows.forEach { week ->
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceEvenly
//            ) {
//                week.forEach { day ->
//                    Box(
//                        modifier = Modifier
//                            .size(40.dp) // Tamanho de cada célula
////                            .background(Color.LightGray)
//                                ,
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(text = day.toString(), fontSize = 16.sp, color = Color.Black)
//                    }
//                }
//
//                // Preenche células vazias se uma semana tiver menos de 7 dias
//                repeat(7 - week.size) {
//                    Spacer(modifier = Modifier.size(40.dp))
//                }
//            }
//            Spacer(modifier = Modifier.height(8.dp)) // Espaçamento entre semanas
//        }
//    }
    }
}
