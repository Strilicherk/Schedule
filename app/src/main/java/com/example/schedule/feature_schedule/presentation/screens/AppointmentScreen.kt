package com.example.schedule.feature_schedule.presentation.screens

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.schedule.feature_schedule.domain.model.Appointment
import com.example.schedule.feature_schedule.presentation.events.AppointmentEvents
import com.example.schedule.feature_schedule.presentation.view_model.AppointmentViewModel

@Composable
fun CreateOrUpdateAppointmentScreen(
    navController: NavController
) {
    val themeColors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(themeColors.primary)
    ) {
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(
                    themeColors.background,
                    RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                )
        ) {
            MainBox()
        }

        Column() {

        }

        Row {
            Buttons()
        }
    }
}

@Composable
fun MainBox() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .padding(horizontal = 25.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            TitleLabel()
            ColorChoose()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Text("Start")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
        ) {
            Text("End")
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
        ) {
            Text("Time | All Day")
        }
    }
}

@Composable
fun TitleLabel() {
    var text by remember { mutableStateOf("") }
    BasicTextField(
        value = text,
        onValueChange = { newValue -> text = newValue },
        textStyle = TextStyle(color = White),
        decorationBox = { innerTextField ->
            if (text.isEmpty()) {
                Text(
                    text = "Title",
                    color = White,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize
                )
            }
            innerTextField()
        },
        singleLine = true,
        cursorBrush = SolidColor(White),
    )
}

@Composable
fun ColorChoose(
    viewModel: AppointmentViewModel = hiltViewModel(),
) {
    var showColorsList by remember { mutableStateOf(false) }
    val state by viewModel.state.collectAsState()
    val appointmentColors = Appointment.noteColors
    val rows = appointmentColors.chunked(4)

    Column(
        horizontalAlignment = Alignment.End
    ) {
        Button(
            onClick = { showColorsList = !showColorsList },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = state.appointmentColor),
            modifier = Modifier
                .size(20.dp)
        ) {}

        if (showColorsList) {
            Popup(
                onDismissRequest = { showColorsList = false }
            ) {
                Box(
                    modifier = Modifier
                        .padding(20.dp, 30.dp)
                        .size(150.dp, 80.dp)
                        .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(15.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                    ) {
                        rows.forEach { colorsList ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp, 5.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                colorsList.forEach { color ->
                                    Button(
                                        onClick = {
                                            viewModel.onEvent(AppointmentEvents.SelectAppointmentColor(color))
                                            showColorsList = false
                                        },
                                        shape = CircleShape,
                                        colors = ButtonDefaults.buttonColors(containerColor = color),
                                        modifier = Modifier.size(25.dp)
                                    ) {}
                                }
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun InfoLabel() {

}

@Composable
fun Buttons() {

}