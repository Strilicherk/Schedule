package com.example.schedule.feature_schedule.presentation.view_add_edit_appointment.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.schedule.ui.theme.CustomGray

@Composable
fun CreateOrUpdateAppointmentScreen() {
    val themeColors = MaterialTheme.colorScheme
    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(themeColors.primary)
    ) {
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(themeColors.background, RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp))
        ) {
            MainBox()
        }

        LazyColumn() {

        }

        Row {
            Buttons()
        }
    }
}

@Composable
fun MainBox() {
    var text by remember { mutableStateOf("") }

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.75f)
    ) {
        Row (
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxWidth()
                .height(100.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = { newValue -> text = newValue },
                modifier = Modifier,
                textStyle = TextStyle(color = White),
                decorationBox = { innerTextField ->
                    if (text.isEmpty()) {
                        Text("Title", color = CustomGray)
                    }
                    innerTextField()
                },
                singleLine = true,
                cursorBrush = SolidColor(White),
            )


        }

        Row (
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Text("Start")
        }

        Row (
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Text("End")
        }

        Row (
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .fillMaxWidth()
                .height(55.dp)
        ) {
            Text("Time | All Day")
        }
    }
}

@Composable
fun InfoLabel() {

}

@Composable
fun Buttons() {

}
