package com.example.schedule.feature_schedule.presentation.schedule_menu.components

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontVariation.width
import androidx.compose.ui.unit.dp
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
            .background(Gray, shape = RoundedCornerShape(20.dp)),
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
    columns: Int = 7,
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
//    Log.d(TAG, "$alterna")
    val daysOfWeek = listOf("S","M", "T", "W", "T", "F", "S")
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
            rows.forEach { week ->
                Row(
                    modifier = Modifier
//                        .background(if (alterna % 2 == 0) Red else Green)
                        .fillMaxWidth()
                ) {
                    week.forEach { day ->
                        alterna++
                        Box (
                            modifier = Modifier
//                                .weight(weight = 1f)
                                .width(54.dp)
                                .height(60.dp)
//                                .background(if (alterna % 2 == 0) Black else White)

                        ) {
                            Text(
                                text = day.toString(),
                                modifier = Modifier
//                                    .background(Blue)
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
