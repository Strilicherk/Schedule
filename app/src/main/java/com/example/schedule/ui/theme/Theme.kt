package com.example.schedule.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color.Companion.White
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorScheme = darkColorScheme(
    primary = CustomBlack,
    secondary = CustomCarbon,
    tertiary = CustomGray,
    background = CustomCarbon,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = White,
)

@Composable
fun CalendarTheme(content: @Composable () -> Unit) {
    val systemUiController = rememberSystemUiController()
    val colors = DarkColorScheme

    systemUiController.setStatusBarColor(
        color = colors.background,
        darkIcons = false
    )

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}