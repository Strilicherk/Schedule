package com.example.schedule.feature_schedule.presentation.schedule_menu.components

import android.util.Log
import android.widget.GridLayout
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val TAG = "MonthDebugScreen"

@Composable
fun MonthScheduleGridComponent() {
//    val itemList = listOf("Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro")
    val itemList = listOf("Janeiro")
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.4f)
            .background(Gray, shape = RoundedCornerShape(20.dp))
    ) {
        items(items = itemList, itemContent = {item ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                GridItem()
            }
        })
    }
}

@Composable
fun GridItem(
    columns: Int = 7,
    rows: Int = 5
) {
    val daysList = (1..30).map { "Item $it" } // Lista de itens para mostrar no grid
    // Divida os itens em linhas com base no número de colunas
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        daysList.chunked(columns).forEach { rowItems ->
                daysList.forEach { item ->
                    Box(
                        modifier = Modifier
                            .background(Red)
                            .fillMaxSize()
                    ) {
                        Text(text = item)
                    }
                }

//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(vertical = 4.dp),
//                horizontalArrangement = Arrangement.spacedBy(8.dp)
//            ) {
//                rowItems.forEach { item ->
//                    Box(
//                        modifier = Modifier
//                            .weight(1f) // Define a largura de cada item de forma igual
//                            .aspectRatio(1f) // Mantém cada item quadrado
//                            .background(Color.LightGray),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(text = item, fontSize = 16.sp)
//                    }
//                }
//                // Preenche células vazias se os itens não completarem uma linha
//                repeat(columns - rowItems.size) {
//                    Spacer(modifier = Modifier.weight(1f))
//                }
//            }
        }
    }
}
